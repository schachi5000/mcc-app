package net.schacher.mcc.shared.screens.main

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.repositories.PackRepository
import net.schacher.mcc.shared.screens.main.MainUiState.FullScreen.DeckScreen
import net.schacher.mcc.shared.screens.main.MainUiState.MainScreen.Decks
import net.schacher.mcc.shared.screens.main.MainUiState.MainScreen.Search
import net.schacher.mcc.shared.screens.main.MainUiState.MainScreen.Settings
import net.schacher.mcc.shared.screens.main.MainUiState.MainScreen.Spotlight
import net.schacher.mcc.shared.screens.main.MainUiState.Splash
import net.schacher.mcc.shared.screens.main.MainUiState.SubScreen.CardMenu

class MainViewModel(
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository,
    private val packRepository: PackRepository
) : ViewModel() {

    private companion object {
        const val SPLASH_DELAY_MS = 2000L
    }

    private val _state =
        MutableStateFlow(MainUiState(splash = Splash(cardRepository.cards.isEmpty())))

    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Event>()

    val event = _event.asSharedFlow()

    init {
        // TODO This needs to be prettier
        this.viewModelScope.launch {
            delay(1000)
            if (cardRepository.cards.isEmpty()) {
                try {
                    cardRepository.refreshAllCards()
                    packRepository.refreshAllPacks()
                    _event.emit(Event.DatabaseSynced)
                } catch (e: Exception) {
                    Logger.e(e) { "Error refreshing cards" }
                    _event.emit(Event.CardsDatabaseSyncFailed(e))
                }
            }
        }

        this.viewModelScope.launch {
            if (cardRepository.cards.isEmpty()) {
                delay(SPLASH_DELAY_MS)
            }

            _state.update {
                it.copy(
                    splash = null,
                    mainScreen = Decks
                )
            }
        }
    }

    fun onTabSelected(tabIndex: Int) {
        this.viewModelScope.launch {
            val mainScreen = when (tabIndex) {
                0 -> Decks
                1 -> Spotlight
                2 -> Search
                3 -> Settings
                else -> return@launch
            }

            _state.update {
                it.copy(mainScreen = mainScreen)
            }
        }
    }

    fun onCardClicked(card: Card) {
        this.viewModelScope.launch {
            _state.update { it.copy(subScreen = CardMenu(card)) }
        }
    }


    fun onDeckClicked(deck: Deck) {
        this.viewModelScope.launch {
            _state.update { it.copy(fullScreen = DeckScreen(deck)) }
        }
    }

    fun onContextMenuClosed() {
        this.viewModelScope.launch {
            _state.update { it.copy(subScreen = null) }
        }
    }

    fun onRemoveDeckClick(deck: Deck) {
        this.viewModelScope.launch {
            deckRepository.removeDeck(deck)
            _state.update { it.copy(subScreen = null) }
        }
    }

    fun onBackPressed() {
        this.viewModelScope.launch {
            _state.update {
                it.copy(
                    subScreen = null,
                    fullScreen = null
                )
            }
        }
    }

    fun onPackSelectionClicked() {
        this._state.update {
            it.copy(fullScreen = MainUiState.FullScreen.PackSelectionScreen)
        }
    }
}

sealed interface Event {
    data object DatabaseSynced : Event
    data class CardsDatabaseSyncFailed(val exception: Exception) : Event
}

data class MainUiState(
    val splash: Splash? = null,
    val mainScreen: MainScreen = Spotlight,
    val subScreen: SubScreen? = null,
    val fullScreen: FullScreen? = null
) {
    data class Splash(val preparing: Boolean) : FullScreen

    sealed interface MainScreen {
        data object Decks : MainScreen
        data object Spotlight : MainScreen
        data object Search : MainScreen
        data object Settings : MainScreen
    }

    sealed interface SubScreen {
        data class CardMenu(val card: Card) : SubScreen

        data class DeckMenu(val deck: Deck) : SubScreen

        data class CardDetails(val card: Card) : SubScreen
    }

    sealed interface FullScreen {
        data class DeckScreen(val deck: Deck) : FullScreen
        data object PackSelectionScreen : FullScreen
    }
}
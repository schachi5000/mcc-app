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
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.repositories.PackRepository
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.FullScreen.CreateDeck
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.FullScreen.DeckScreen
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.Decks
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.Search
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.Settings
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.Splash
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.SubScreen.CardMenu

class MainViewModel(
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository,
    private val packRepository: PackRepository
) : ViewModel() {

    private companion object {
        const val SPLASH_DELAY_MS = 2000L
    }

    private val _state = MutableStateFlow(
        UiState(
            mainScreen = Decks,
            splash = Splash(cardRepository.cards.value.isEmpty())
        )
    )

    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Event>()

    val event = _event.asSharedFlow()

    init {
        // TODO This needs to be prettier
        this.viewModelScope.launch {
            delay(1000)
            if (cardRepository.cards.value.isEmpty()) {
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
            if (cardRepository.cards.value.isEmpty()) {
                delay(SPLASH_DELAY_MS)
            }

            _state.update {
                it.copy(splash = null)
            }
        }
    }

    fun onTabSelected(tabIndex: Int) {
        this.viewModelScope.launch {
            val mainScreen = when (tabIndex) {
                0 -> Decks
                1 -> UiState.MainScreen.Spotlight
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

    fun onRemoveDeckClick(deckId: Int) {
        this.viewModelScope.launch {
            deckRepository.removeDeck(deckId)
            _state.update { it.copy(fullScreen = null) }
        }
    }

    fun onNewDeckClicked() {
        this.viewModelScope.launch {
            _state.update {
                val values = cardRepository.cards.value.values
                    .filter { it.type == CardType.HERO }.toSet()

                it.copy(fullScreen = CreateDeck(values))
            }
        }
    }

    fun onNewDeckHeroSelected(hero: Card, aspect: Aspect? = null) {
        this.viewModelScope.launch {
            try {
                deckRepository.createDeck(heroCard = hero, aspect = aspect)
                _state.update {
                    it.copy(fullScreen = null)
                }
                _event.emit(Event.DeckCreated(hero.name))
            } catch (e: Exception) {
                Logger.e(e) { "Error creating deck for card $hero" }
            }
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
            it.copy(fullScreen = UiState.FullScreen.PackSelectionScreen)
        }
    }

    sealed interface Event {
        data object DatabaseSynced : Event
        data class DeckCreated(val deckName: String) : Event

        data class CardsDatabaseSyncFailed(val exception: Exception) : Event
    }

    data class UiState internal constructor(
        val splash: Splash? = null,
        val mainScreen: MainScreen,
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

        }

        sealed interface FullScreen {
            data class DeckScreen(val deck: Deck) : FullScreen

            data object PackSelectionScreen : FullScreen

            data class CreateDeck(val heroCodes: Set<Card>) : FullScreen
        }
    }
}
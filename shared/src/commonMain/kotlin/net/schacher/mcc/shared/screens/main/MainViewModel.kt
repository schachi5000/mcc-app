package net.schacher.mcc.shared.screens.main

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.screens.main.MainUiState.MainScreen.Decks
import net.schacher.mcc.shared.screens.main.MainUiState.MainScreen.Featured
import net.schacher.mcc.shared.screens.main.MainUiState.MainScreen.Search
import net.schacher.mcc.shared.screens.main.MainUiState.MainScreen.Settings
import net.schacher.mcc.shared.screens.main.MainUiState.Splash
import net.schacher.mcc.shared.screens.main.MainUiState.SubScreen.CardMenu
import net.schacher.mcc.shared.screens.main.MainUiState.SubScreen.DeckInspector

class MainViewModel(
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState(Splash(cardRepository.cards.isEmpty())))

    val state = _state.asStateFlow()

    init {
        this.viewModelScope.launch {
            if (cardRepository.cards.isEmpty()) {
                try {
                    cardRepository.refresh()
                } catch (e: Exception) {
                    Logger.e(e) { "Error refreshing cards" }
                }
            } else {
                delay(2000)
            }
            _state.update {
                it.copy(splash = null, mainScreen = Decks)
            }
        }
    }

    fun onTabSelected(tabIndex: Int) {
        this.viewModelScope.launch {
            val mainScreen = when (tabIndex) {
                0 -> Decks
                1 -> Featured
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
            _state.update { it.copy(subScreen = DeckInspector(deck)) }
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
}


data class MainUiState(
    val splash: Splash? = null,
    val mainScreen: MainScreen = Featured,
    val subScreen: SubScreen? = null
) {
    data class Splash(val preparing: Boolean)

    sealed interface MainScreen {
        data object Decks : MainScreen
        data object Featured : MainScreen
        data object Search : MainScreen
        data object Settings : MainScreen
    }

    sealed interface SubScreen {
        data class CardMenu(val card: Card) : SubScreen

        data class DeckMenu(val deck: Deck) : SubScreen

        data class DeckInspector(val deck: Deck) : SubScreen

        data class CardDetails(val card: Card) : SubScreen
    }
}
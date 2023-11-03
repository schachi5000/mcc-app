package net.schacher.mcc.shared.screens.main

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.screens.main.MainUiState.ContextMenu.CardMenu
import net.schacher.mcc.shared.screens.main.MainUiState.ContextMenu.DeckMenu

class MainViewModel(
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState(preparingApp = this.cardRepository.cards.isEmpty()))

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            if (cardRepository.cards.isEmpty()) {
                _state.update { it.copy(preparingApp = true) }
                cardRepository.refresh()
                _state.update { it.copy(preparingApp = false) }
            }
        }
    }

    fun onTabSelected(tabIndex: Int) {
        viewModelScope.launch {
            _state.update { it.copy(selectedTabIndex = tabIndex) }
        }
    }

    fun onCardClicked(card: Card) {
        viewModelScope.launch {
            _state.update { it.copy(contextMenu = CardMenu(card)) }
        }
    }


    fun onDeckClicked(deck: Deck) {
        viewModelScope.launch {
            _state.update { it.copy(contextMenu = DeckMenu(deck)) }
        }
    }

    fun onContextMenuClosed() {
        viewModelScope.launch {
            _state.update { it.copy(contextMenu = null) }
        }
    }

    fun onRemoveDeckClick(deck: Deck) {
        viewModelScope.launch {
            deckRepository.removeDeck(deck)
            _state.update { it.copy(contextMenu = null) }
        }
    }
}


data class MainUiState(
    val preparingApp: Boolean = true,
    val selectedTabIndex: Int = 1,
    val contextMenu: ContextMenu? = null
) {

    sealed interface ContextMenu {
        data class CardMenu(val card: Card) : ContextMenu
        data class DeckMenu(val deck: Deck) : ContextMenu
    }
}
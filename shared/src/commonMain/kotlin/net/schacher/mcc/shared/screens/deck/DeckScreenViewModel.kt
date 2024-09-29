package net.schacher.mcc.shared.screens.deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.utils.debug

class DeckScreenViewModel(
    deckId: Int,
    private val deckRepository: DeckRepository
) : ViewModel() {

    init {
        Logger.debug { "DeckScreenViewModel init" }
    }

    private val _state = MutableStateFlow(
        UiState(
            deck = deckRepository.getDeckById(deckId)
                ?: throw IllegalArgumentException("Deck not found")
        )
    )

    val state = _state.asStateFlow()

    fun onCardOptionClicked(card: Card) {
        _state.value = _state.value.copy(selectedCard = card)
    }

    fun onCardOptionDismissed() {
        _state.value = _state.value.copy(selectedCard = null)
    }

    fun onRemoveCardFromDeck(cardId: String) {
        this.viewModelScope.launch {
            _state.update {
                it.copy(selectedCard = null)
            }

            val updatedDeck = deckRepository.removeCardFromDeck(state.value.deck.id, cardId)

            _state.update {
                it.copy(
                    deck = updatedDeck,
                    selectedCard = null
                )
            }
        }
    }

    data class UiState(
        val deck: Deck,
        val selectedCard: Card? = null
    )
}
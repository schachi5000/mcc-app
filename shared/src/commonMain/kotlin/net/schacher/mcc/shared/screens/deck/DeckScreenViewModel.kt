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
import net.schacher.mcc.shared.repositories.SpotlightRepository
import net.schacher.mcc.shared.utils.debug

class DeckScreenViewModel(
    deckId: Int,
    private val deckRepository: DeckRepository,
    spotlightRepository: SpotlightRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(
        UiState(
            deck = deckRepository.getDeckById(deckId) ?: spotlightRepository.getDeckById(deckId)
            ?: throw IllegalArgumentException("Deck not found"),
            options = if (deckRepository.hasDeck(deckId)) {
                setOf(UiState.Option.REMOVE)
            } else {
                emptySet()
            }
        )
    )

    val state = _state.asStateFlow()

    fun onShowCardOptionClicked(card: Card) {
        _state.value = _state.value.copy(selectedCard = card)
    }

    fun onCardOptionDismissed() {
        _state.value = _state.value.copy(selectedCard = null)
    }

    fun onOptionClicked(option: UiState.Option) {
        when (option) {
            UiState.Option.REMOVE -> {
                _state.value.selectedCard?.let {
                    this.removeCardFromDeck(it.code)
                }
            }
        }
    }

    private fun removeCardFromDeck(cardCode: String) {
        this.viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedCard = null,
                    loading = true
                )
            }

            val updatedDeck = runCatching {
                deckRepository.removeCardFromDeck(
                    deckId = state.value.deck.id,
                    cardCode = cardCode
                )
            }.getOrNull() ?: state.value.deck

            _state.update {
                it.copy(
                    deck = updatedDeck,
                    selectedCard = null,
                    loading = false
                )
            }
        }
    }

    data class UiState(
        val deck: Deck,
        val selectedCard: Card? = null,
        val options: Set<Option>,
        val loading: Boolean = false,
    ) {
        enum class Option {
            REMOVE
        }
    }
}
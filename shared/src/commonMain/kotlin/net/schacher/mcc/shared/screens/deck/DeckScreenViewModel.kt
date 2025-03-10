package net.schacher.mcc.shared.screens.deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.model.Faction
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.screens.deck.DeckScreenViewModel.UiState.CardOption.REMOVE
import net.schacher.mcc.shared.screens.deck.DeckScreenViewModel.UiState.DeckOption.DELETE
import net.schacher.mcc.shared.screens.deck.DeckScreenViewModel.UiState.Loading.DeletingDeck
import net.schacher.mcc.shared.screens.deck.DeckScreenViewModel.UiState.Loading.RemovingCard
import net.schacher.mcc.shared.utils.defaultSort

class DeckScreenViewModel(
    deckId: Int,
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<UiState?>(runBlocking {
        val deck = deckRepository.getDeckById(deckId)
            ?: throw IllegalArgumentException("Deck not found")
        val userDeck = isUserDeck(deckId)

        UiState(
            deck = deck,
            cardOptions = if (userDeck) {
                setOf(REMOVE)
            } else {
                emptySet()
            },
            cards = cardRepository.getCards(deck.cardCodes),
            deckOptions = if (userDeck) {
                setOf(DELETE)
            } else {
                emptySet()
            },
        )
    })

    val state = _state.asStateFlow()

    private fun isUserDeck(deckId: Int): Boolean = this.deckRepository.hasDeck(deckId)

    fun onDeleteDeckClicked() {
        val deckId = this.state.value?.deck?.id ?: return

        this.viewModelScope.launch {
            _state.update {
                it?.copy(loading = DeletingDeck)
            }
            if (deckRepository.removeDeck(deckId)) {
                _state.update { null }
            } else {
                _state.update {
                    it?.copy(loading = null)
                }
            }
        }
    }

    fun onShowCardOptionClicked(card: Card) {
        _state.value = _state.value?.copy(selectedCard = card)
    }

    fun onCardOptionDismissed() {
        _state.value = _state.value?.copy(selectedCard = null)
    }

    fun onOptionClicked(cardOption: UiState.CardOption) {
        when (cardOption) {
            REMOVE -> {
                _state.value?.selectedCard?.let {
                    this.removeCardFromDeck(it.code)
                }
            }

            else -> {}
        }
    }

    private fun removeCardFromDeck(cardCode: String) {
        val state = state.value ?: return

        this.viewModelScope.launch {
            _state.update {
                it?.copy(
                    selectedCard = null,
                    loading = RemovingCard
                )
            }

            val updatedDeck = runCatching {
                deckRepository.removeCardFromDeck(
                    deckId = state.deck.id,
                    cardCode = cardCode
                )
            }.getOrNull() ?: state.deck

            _state.update {
                state.copy(
                    deck = updatedDeck,
                    selectedCard = null,
                    loading = null
                )
            }
        }
    }

    data class UiState(
        val deck: Deck,
        val cards: List<Card>,
        val selectedCard: Card? = null,
        val cardOptions: Set<CardOption>,
        val deckOptions: Set<DeckOption>,
        val loading: Loading? = null,
    ) {
        val heroCards = cards
            .filter { it.type != CardType.HERO && it.setCode == deck.hero.setCode }
            .sortedBy { it.cost ?: 0 }

        val aspectCards: List<Card> = this.cards
            .filter { it.setCode != deck.hero.setCode && it.aspect != null }
            .defaultSort()

        val basicCards: List<Card> = this.cards
            .filter { it.setCode != deck.hero.setCode && it.faction == Faction.BASIC }
            .defaultSort()

        enum class CardOption {
            REMOVE
        }

        enum class DeckOption {
            DELETE
        }

        sealed interface Loading {
            data object RemovingCard : Loading
            data object DeletingDeck : Loading
        }
    }
}
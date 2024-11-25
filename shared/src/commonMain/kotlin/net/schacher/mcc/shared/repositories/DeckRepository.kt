package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.utils.launchAndCollect
import net.schacher.mcc.shared.utils.replace

class DeckRepository(
    private val cardRepository: CardRepository,
    private val marvelCDbDataSource: MarvelCDbDataSource,
    authRepository: AuthRepository
) {
    private val scope: CoroutineScope = MainScope()

    private val _decks = MutableStateFlow<List<Deck>>(emptyList())

    val decks = _decks.asStateFlow()

    init {
        this.scope.launchAndCollect(authRepository.loginState) {
            if (it) {
                refreshAllUserDecks()
            } else {
                _decks.emit(emptyList())
            }
        }
    }

    fun hasDeck(deckId: Int): Boolean = this.getDeckById(deckId) != null

    fun getDeckById(deckId: Int): Deck? = this.decks.value.find { it.id == deckId }

    fun createDeck(heroCard: Card, label: String? = null, aspect: Aspect? = null) {
        // TODO:
    }

    fun removeDeck(deckId: Int) {
        _decks.update {
            it.toMutableList().also {
                it.removeAll { it.id == deckId }
            }
        }
    }

    suspend fun addCardToDeck(deckId: Int, cardCode: String) {
        val card = this.cardRepository.getCard(cardCode)
        val deck = this.decks.value.find { it.id == deckId }
            ?: throw IllegalArgumentException("Deck with id $deckId not found")

        val newDeck = deck.copy(
            cards = deck.cards + card
        )

        val updateDeck = this.marvelCDbDataSource.updateDeck(newDeck) {
            cardRepository.getCard(it)
        }.getOrThrow()

        _decks.update { it.toMutableList().replace(deck, updateDeck) }
    }

    suspend fun removeCardFromDeck(deckId: Int, cardCode: String): Deck {
        val card = this.cardRepository.getCard(cardCode)
        val deck = this.decks.value.find { it.id == deckId }
            ?: throw IllegalArgumentException("Deck with id $deckId not found")

        if (!deck.cards.contains(card)) {
            throw IllegalArgumentException("Card with code $cardCode not found in deck with id $deckId")
        }

        val newDeck = deck.copy(
            cards = deck.cards - card
        )

        val updateDeck = this.marvelCDbDataSource.updateDeck(newDeck) {
            cardRepository.getCard(it)
        }.getOrThrow()

        _decks.update { it.toMutableList().replace(deck, updateDeck) }
        return updateDeck
    }

    suspend fun refreshAllUserDecks() {
        val decks = this.marvelCDbDataSource.getUserDecks {
            this.cardRepository.getCard(it)
        }.getOrNull() ?: return

        _decks.emit(decks)
    }
}
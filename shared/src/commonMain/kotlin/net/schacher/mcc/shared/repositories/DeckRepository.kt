package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.schacher.mcc.shared.AppLogger
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.utils.launchAndCollect
import net.schacher.mcc.shared.utils.replace

class DeckRepository(
    private val cardRepository: CardRepository,
    private val spotlightRepository: SpotlightRepository,
    private val marvelCDbDataSource: MarvelCDbDataSource,
    authRepository: AuthRepository
) {
    private companion object {
        const val TAG = "DeckRepository"
    }

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

    fun getDeckById(deckId: Int): Deck? {
        this.spotlightRepository.getDeckById(deckId)?.let {
            return it
        }

        return this.decks.value.find { it.id == deckId }
    }

    suspend fun getCardsInDeck(deckId: Int): List<Card> {
        this.spotlightRepository.getDeckById(deckId)?.let {
            return cardRepository.getCards(it.cardCodes)
        }

        this.getDeckById(deckId)?.let {
            return cardRepository.getCards(it.cardCodes)
        }

        return emptyList()
    }

    fun getDecksContainingCard(cardCode: String): List<Deck> =
        this.decks.value.filter {
            it.hero.code == cardCode || it.cardCodes.any { it == cardCode }
        }

    suspend fun createDeck(heroCardCode: String, label: String? = null): Result<Int> =
        this.marvelCDbDataSource.createDeck(heroCardCode, label).also {
            val deckId = it.getOrNull()
            if (deckId != null) {
                AppLogger.d(TAG) { "Deck[$deckId] created successfully" }
                refreshAllUserDecks()
            }
        }

    suspend fun removeDeck(deckId: Int): Boolean =
        this.marvelCDbDataSource.deleteDeck(deckId).also {
            if (it.isSuccess) {
                AppLogger.d(TAG) { "Deck[$deckId] deleted successfully" }
                _decks.update { decks ->
                    decks.toMutableList()
                        .also { it.removeAll { deck -> deck.id == deckId } }
                }
                refreshAllUserDecks()
            }
        }.isSuccess

    suspend fun addCardToDeck(deckId: Int, cardCode: String) {
        val card = this.cardRepository.getCard(cardCode)
        val deck = this.decks.value.find { it.id == deckId }
            ?: throw IllegalArgumentException("Deck with id $deckId not found")

        val newDeck = deck.copy(
            cardCodes = deck.cardCodes + card.code
        )

        val updateDeck = this.marvelCDbDataSource.updateDeck(newDeck) {
            cardRepository.getCards(it)
        }.getOrThrow()

        _decks.update { it.toMutableList().replace(deck, updateDeck) }
    }

    suspend fun removeCardFromDeck(deckId: Int, cardCode: String): Deck {
        val card = this.cardRepository.getCard(cardCode)
        val deck = this.decks.value.find { it.id == deckId }
            ?: throw IllegalArgumentException("Deck with id $deckId not found")

        if (!deck.cardCodes.contains(card.code)) {
            throw IllegalArgumentException("Card with code $cardCode not found in deck with id $deckId")
        }

        val newDeck = deck.copy(
            cardCodes = deck.cardCodes - card.code
        )

        val updateDeck = this.marvelCDbDataSource.updateDeck(newDeck) {
            cardRepository.getCards(it)
        }.getOrThrow()

        _decks.update { it.toMutableList().replace(deck, updateDeck) }
        return updateDeck
    }

    suspend fun refreshAllUserDecks() {
        val decks = this.marvelCDbDataSource.getUserDecks {
            this.cardRepository.getCards(it)
        }.getOrNull() ?: return

        _decks.emit(decks)
    }
}
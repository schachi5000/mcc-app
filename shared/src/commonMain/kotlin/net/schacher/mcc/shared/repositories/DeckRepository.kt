package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import net.schacher.mcc.shared.datasource.database.DeckDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType.HERO
import net.schacher.mcc.shared.model.Deck
import kotlin.random.Random

class DeckRepository(
    private val cardRepository: CardRepository,
    private val marvelCDbDataSource: MarvelCDbDataSource,
    private val deckDatabaseDao: DeckDatabaseDao
) {
    private val _state = MutableStateFlow(deckDatabaseDao.getDecks())

    val state = _state.asStateFlow()

    val decks: List<Deck>
        get() = this.state.value

    private val randomDeckNumber: Int
        get() = Random.nextInt(Int.MAX_VALUE) * -1

    fun createDeck(label: String, aspect: Aspect, heroCard: Card) {
        if (heroCard.type != HERO) {
            throw Exception("Hero card must be of type HERO - $heroCard")
        }

        val deck = Deck(randomDeckNumber, label, heroCard, aspect, listOf(heroCard))
        this.deckDatabaseDao.addDeck(deck)
        _state.update { deckDatabaseDao.getDecks() }
    }

    fun removeDeck(deck: Deck) {
        this.deckDatabaseDao.removeDeck(deck.id)
        _state.update { deckDatabaseDao.getDecks() }
    }

    fun addCardToDeck(deckId: Int, cardCode: String) {
        val card = this.cardRepository.getCard(cardCode)
        checkNotNull(card) {
            "Card with code $cardCode not found"
        }
        val deck = this.decks.find { it.id == deckId }
        checkNotNull(deck) { "Deck with id $deckId not found" }

        val newDeck = deck.copy(
            cards = deck.cards + card
        )

        this.deckDatabaseDao.addDeck(newDeck)
        _state.update { deckDatabaseDao.getDecks() }
    }

    suspend fun addDeckById(deckId: Int) {
        val deck = marvelCDbDataSource.getPublicDeckById(deckId) {
            this.cardRepository.getCard(it)
        }

        this.deckDatabaseDao.addDeck(deck)
        _state.emit(deckDatabaseDao.getDecks())
    }

    suspend fun deleteAllDeckData() = withContext(Dispatchers.IO) {
        deckDatabaseDao.wipeDeckTable()
        _state.emit(deckDatabaseDao.getDecks())
    }
}
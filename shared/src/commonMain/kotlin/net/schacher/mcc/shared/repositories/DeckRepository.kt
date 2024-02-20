package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val _decks = MutableStateFlow<List<Deck>>(emptyList())

    val decks = _decks.asStateFlow()

    init {
        MainScope().launch {
            _decks.emit(deckDatabaseDao.getDecks())
        }
    }

    private val randomDeckNumber: Int
        get() = Random.nextInt(Int.MAX_VALUE) * -1

    suspend fun createDeck(heroCard: Card, label: String? = null, aspect: Aspect? = null) {
        if (heroCard.type != HERO) {
            throw Exception("Hero card must be of type HERO - $heroCard")
        }

        val deckLabel = label ?: "${heroCard.name} ${aspect?.name ?: ""}"

        val deck = Deck(randomDeckNumber, deckLabel, heroCard, aspect, listOf(heroCard))
        this.deckDatabaseDao.addDeck(deck)

        _decks.update { deckDatabaseDao.getDecks() }
    }

    suspend fun removeDeck(deck: Deck) {
        this.deckDatabaseDao.removeDeck(deck.id)

        _decks.update { deckDatabaseDao.getDecks() }
    }

    suspend fun addCardToDeck(deckId: Int, cardCode: String) {
        val card = this.cardRepository.getCard(cardCode)
        val deck = this.decks.value.find { it.id == deckId }
        checkNotNull(deck) { "Deck with id $deckId not found" }

        val newDeck = deck.copy(
            cards = deck.cards + card
        )

        this.deckDatabaseDao.addDeck(newDeck)

        _decks.update { deckDatabaseDao.getDecks() }
    }

    suspend fun addDeckById(deckId: Int) {
        val deck = marvelCDbDataSource.getPublicDeckById(deckId) {
            this.cardRepository.getCard(it)
        }

        this.deckDatabaseDao.addDeck(deck)
        _decks.emit(deckDatabaseDao.getDecks())
    }

    suspend fun deleteAllDeckData() = withContext(Dispatchers.IO) {
        deckDatabaseDao.wipeDeckTable()
        _decks.emit(deckDatabaseDao.getDecks())
    }
}
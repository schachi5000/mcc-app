package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import net.schacher.mcc.shared.datasource.database.DatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType.HERO
import net.schacher.mcc.shared.model.Deck
import kotlin.random.Random

class DeckRepository(
    private val cardRepository: CardRepository,
    private val marvelCDbDataSource: MarvelCDbDataSource,
    private val databaseDao: DatabaseDao
) {
    private val _state = MutableStateFlow(databaseDao.getDecks())

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
        this.databaseDao.addDeck(deck)
        _state.update { databaseDao.getDecks() }
    }

    fun removeDeck(deck: Deck) {
        this.databaseDao.removeDeck(deck.id)
        _state.update { databaseDao.getDecks() }
    }

    suspend fun addDeckById(deckId: Int) {
        val deck = marvelCDbDataSource.getPublicDeckById(deckId) {
            this.cardRepository.getCard(it)
        }

        this.databaseDao.addDeck(deck)
        _state.emit(databaseDao.getDecks())
    }

    suspend fun deleteAllDecks() = withContext(Dispatchers.IO) {
        databaseDao.removeAllDecks()
        _state.emit(databaseDao.getDecks())
    }
}
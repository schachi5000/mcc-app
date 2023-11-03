package net.schacher.mcc.shared.repositories

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import net.schacher.mcc.shared.datasource.database.DatabaseDao
import net.schacher.mcc.shared.datasource.http.KtorCardDataSource
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType.HERO
import net.schacher.mcc.shared.model.Deck
import kotlin.random.Random

class DeckRepository(
    private val cardRepository: CardRepository,
    private val databaseDao: DatabaseDao
) {
    val decks: List<Deck>
        get() = this.databaseDao.getDecks()

    private val randomDeckNumber: Int
        get() = Random.nextInt(Int.MAX_VALUE) * -1

    fun createDeck(label: String, heroCard: Card) {
        if (heroCard.type != HERO) {
            throw Exception("Hero card must be of type HERO - $heroCard")
        }

        val deck = Deck(randomDeckNumber, label, heroCard, listOf(heroCard))
        this.databaseDao.addDeck(deck)
    }

    fun removeDeck(deck: Deck) {
        this.databaseDao.removeDeck(deck.id)
    }

    fun addDummyDeck() {
        val cards = cardRepository.cards.take(10)
        databaseDao.addDeck(
            Deck(
                randomDeckNumber,
                "deck1",
                cardRepository.cards.first { it.type == HERO },
                cards
            )
        )
        databaseDao.getDecks().forEach {
            Logger.d { "Deck: $it" }
        }
    }

    suspend fun addDeckById(deckId: Int) {
        val deck = KtorCardDataSource.getPublicDeckById(deckId) {
            this.databaseDao.getCardByCode(it)
        }

        this.databaseDao.addDeck(deck)
    }

    suspend fun deleteAllDecks() = withContext(Dispatchers.IO) {
        databaseDao.removeAllDecks()
    }
}
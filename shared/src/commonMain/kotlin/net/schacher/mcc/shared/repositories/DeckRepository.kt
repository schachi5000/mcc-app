package net.schacher.mcc.shared.repositories

import co.touchlab.kermit.Logger
import net.schacher.mcc.shared.database.DatabaseDao
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck

class DeckRepository(
    private val cardRepository: CardRepository,
    private val databaseDao: DatabaseDao
) {
    val decks: List<Deck>
        get() = this.databaseDao.getDecks()

    fun createDeck(label: String, card: Card) {
        val deck = Deck(label, label, listOf(card))
        this.databaseDao.addDeck(deck)
    }

    fun removeDeck(deck: Deck) {
        this.databaseDao.removeDeck(deck.id)
    }

    fun addDummyDeck() {
        val cards = cardRepository.cards.take(10)
        databaseDao.addDeck(Deck("deck1", "Deck 1", cards))
        databaseDao.getDecks().forEach {
            Logger.d { "Deck: $it" }
        }
    }
}
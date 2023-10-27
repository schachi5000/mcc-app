package net.schacher.mcc.shared.repositories

import co.touchlab.kermit.Logger
import net.schacher.mcc.shared.database.DatabaseDao
import net.schacher.mcc.shared.model.Deck

class DeckRepository(
    private val cardRepository: CardRepository,
    private val databaseDao: DatabaseDao
) {
    fun addDummyDeck() {
        val cards = cardRepository.cards.take(10)
        databaseDao.addDeck(Deck("deck1", "Deck 1", cards))
        databaseDao.getDecks().forEach {
            Logger.d { "Deck: $it" }
        }
    }
}
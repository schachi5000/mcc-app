package data

import co.touchlab.kermit.Logger
import database.CardDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import model.Card

class CardRepository(private val cardDatabaseDao: CardDatabaseDao) {

    var cards: List<Card> = this.cardDatabaseDao.getAllCards()
        private set

    suspend fun refresh() {
        withContext(Dispatchers.IO) {
            val result = KtorCardDataSource.getAllCards()
            Logger.d { "${result.size} cards loaded" }
            result.forEach {
                cardDatabaseDao.addCard(it)
            }

            cards = cardDatabaseDao.getAllCards()
        }
    }
}
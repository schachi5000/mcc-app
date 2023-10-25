package repositories

import co.touchlab.kermit.Logger
import database.DatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import model.Card

class CardRepository(private val databaseDao: DatabaseDao) {

    var cards: List<Card> = this.databaseDao.getAllCards()
        private set

    suspend fun refresh() {
        withContext(Dispatchers.IO) {
            val result = KtorCardDataSource.getAllCards()
            Logger.d { "${result.size} cards loaded" }
            databaseDao.addCards(result)
            cards = databaseDao.getAllCards()
        }
    }
}
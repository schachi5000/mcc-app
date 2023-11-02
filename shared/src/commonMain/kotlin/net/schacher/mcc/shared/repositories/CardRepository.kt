package net.schacher.mcc.shared.repositories

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import net.schacher.mcc.shared.database.DatabaseDao
import net.schacher.mcc.shared.datasource.KtorCardDataSource
import net.schacher.mcc.shared.model.Card

class CardRepository(private val databaseDao: DatabaseDao) {

    var cards: List<Card> = this.databaseDao.getAllCards()
        private set

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val result = KtorCardDataSource.getAllCards()
        Logger.d { "${result.size} cards loaded" }
        databaseDao.addCards(result)
        cards = databaseDao.getAllCards()
    }

    suspend fun deleteAllCards() = withContext(Dispatchers.IO) {
        databaseDao.removeAllCards()
        cards = databaseDao.getAllCards()
    }
}
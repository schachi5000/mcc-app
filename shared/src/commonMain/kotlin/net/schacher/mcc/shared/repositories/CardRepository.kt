package net.schacher.mcc.shared.repositories

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import net.schacher.mcc.shared.datasource.database.CardDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Card

class CardRepository(
    private val cardDatabaseDao: CardDatabaseDao,
    private val marvelCDbDataSource: MarvelCDbDataSource
) {

    private val _state = MutableStateFlow(this.cardDatabaseDao.getAllCards())

    val state = _state.asStateFlow()

    val cards: List<Card>
        get() = this.state.value

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val result = marvelCDbDataSource.getAllCards()
        Logger.i { "${result.size} cards loaded" }
        cardDatabaseDao.addCards(result)

        _state.emit(cardDatabaseDao.getAllCards())
    }

    suspend fun deleteAllCardData() = withContext(Dispatchers.IO) {
        cardDatabaseDao.wipeCardTable()
        _state.emit(cardDatabaseDao.getAllCards())
    }

    suspend fun getCard(cardCode: String): Card {
        val card = this.cardDatabaseDao.getCardByCode(cardCode)
        if (card != null) {
            return card
        }

        return this.marvelCDbDataSource.getCard(cardCode).also {
            this.cardDatabaseDao.addCard(it)

            _state.emit(this.cardDatabaseDao.getAllCards())
        }
    }
}
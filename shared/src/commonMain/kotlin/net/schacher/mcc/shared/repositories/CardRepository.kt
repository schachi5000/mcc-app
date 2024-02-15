package net.schacher.mcc.shared.repositories

import co.touchlab.kermit.Logger
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.datasource.database.CardDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Card

class CardRepository(
    private val cardDatabaseDao: CardDatabaseDao,
    private val marvelCDbDataSource: MarvelCDbDataSource
) {

    private val _state = MutableStateFlow<List<Card>>(emptyList())

    val state = _state.asStateFlow()

    val cards: List<Card>
        get() = this.state.value

    init {
        MainScope().launch {
            _state.emit(cardDatabaseDao.getAllCards())
        }
    }

    suspend fun refreshAllCards() {
        val result = this.marvelCDbDataSource.getAllCards()
        Logger.i { "${result.size} cards loaded" }
        this.cardDatabaseDao.addCards(result)

        _state.emit(cardDatabaseDao.getAllCards())
    }

    suspend fun deleteAllCardData() {
        this.cardDatabaseDao.wipeCardTable()
        _state.emit(this.cardDatabaseDao.getAllCards())
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
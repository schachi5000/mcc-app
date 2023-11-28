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
        Logger.d { "${result.size} cards loaded" }
        cardDatabaseDao.addCards(result)

        _state.emit(cardDatabaseDao.getAllCards())
    }

    suspend fun deleteAllCards() = withContext(Dispatchers.IO) {
        cardDatabaseDao.removeAllCards()
        _state.emit(cardDatabaseDao.getAllCards())
    }

    fun getCard(cardCode: String): Card? = this.cards.firstOrNull { it.code == cardCode }

    suspend fun getAndUpdateCard(cardCode: String): Card? {
        val card = this.getCard(cardCode)
        if (card != null) {
            return card
        }

        try {
            this.marvelCDbDataSource.getCard(cardCode).also {
                cardDatabaseDao.addCard(it)
                _state.emit(cardDatabaseDao.getAllCards())
            }
        } catch (e: Exception) {
            Logger.e(e) { "Error loading card: $cardCode" }
        }

        return this.getCard(cardCode)
    }
}
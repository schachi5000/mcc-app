package net.schacher.mcc.shared.repositories

import co.touchlab.kermit.Logger
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.datasource.database.CardDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Card

class CardRepository(
    private val cardDatabaseDao: CardDatabaseDao,
    private val marvelCDbDataSource: MarvelCDbDataSource
) {

    private val _cards = MutableStateFlow<Map<String, Card>>(emptyMap())

    val cards = _cards.asStateFlow()

    init {
        MainScope().launch {
            _cards.emit(cardDatabaseDao.getAllCards().toMap())
        }
    }

    suspend fun hasCards(): Boolean = try {
        cardDatabaseDao.getAllCards().isNotEmpty()
    } catch (e: Exception) {
        Logger.e(e) { "Error checking for cards" }
        false
    }

    suspend fun refreshAllCards() {
        val result = this.marvelCDbDataSource.getAllCards()
        Logger.i { "${result.size} cards loaded" }
        this.cardDatabaseDao.addCards(result)

        _cards.emit(cardDatabaseDao.getAllCards().toMap())
    }

    suspend fun deleteAllCardData() {
        this.cardDatabaseDao.wipeCardTable()
        _cards.emit(this.cardDatabaseDao.getAllCards().toMap())
    }

    suspend fun getCard(cardCode: String): Card {
        this.cards.value[cardCode]?.let {
            return it
        }

        this.cardDatabaseDao.getCardByCode(cardCode)?.let { card ->
            _cards.update { it.toMutableMap().apply { put(cardCode, card) } }
            return card
        }

        return this.marvelCDbDataSource.getCard(cardCode).also {
            this.cardDatabaseDao.addCard(it)
            _cards.emit(this.cardDatabaseDao.getAllCards().toMap())
        }
    }

    fun getCardsBySetCode(setCode: String): List<Card> =
        this.cards.value.values.filter { it.setCode == setCode }.distinctBy { it.code }
}

private fun List<Card>.toMap(): Map<String, Card> = this.associateBy { it.code }
package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.datasource.database.CardDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.utils.launchAndCollect

class CardRepository(
    private val cardDatabaseDao: CardDatabaseDao,
    private val marvelCDbDataSource: MarvelCDbDataSource,
    private val scope: CoroutineScope
) {
    private val _cards = MutableStateFlow<Map<String, Card>>(emptyMap())

    val cards = _cards.asStateFlow()

    init {
        this.scope.launch {
            _cards.emit(cardDatabaseDao.getAllCards().toMap())
        }

        this.scope.launchAndCollect(
            this.cardDatabaseDao.onCardAdded,
            Dispatchers.Default
        ) { card ->
            _cards.update { it.toMutableMap().apply { put(card.code, card) } }
        }
    }

    suspend fun refreshAllCards() {
        this._cards.update {
            cardDatabaseDao.getAllCards().toMap()
        }
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

        return this.marvelCDbDataSource.getCard(cardCode).getOrThrow().also { newCard ->
            this.cardDatabaseDao.addCard(newCard)
            this._cards.update {
                it.toMutableMap().apply { put(cardCode, newCard) }
            }
        }
    }

    fun getCardsBySetCode(setCode: String): List<Card> =
        this.cards.value.values.filter { it.setCode == setCode }.distinctBy { it.code }
}

private fun List<Card>.toMap(): Map<String, Card> = this.associateBy { it.code }
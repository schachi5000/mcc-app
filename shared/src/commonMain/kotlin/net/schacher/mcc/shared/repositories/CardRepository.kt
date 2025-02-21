package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import net.schacher.mcc.shared.datasource.database.CardDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Card

class CardRepository(
    private val cardDatabaseDao: CardDatabaseDao,
    private val marvelCDbDataSource: MarvelCDbDataSource,
    scope: CoroutineScope
) {
    val cards = this.cardDatabaseDao.getCards().stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    suspend fun deleteAllCardData() {
        this.cardDatabaseDao.wipeCardTable()
    }

    suspend fun getCard(cardCode: String): Card {
        this.cardDatabaseDao.getCardByCode(cardCode)?.let { card ->
            return card
        }

        return this.marvelCDbDataSource.getCard(cardCode).getOrThrow().also { newCard ->
            this.cardDatabaseDao.addCard(newCard)
        }
    }
}
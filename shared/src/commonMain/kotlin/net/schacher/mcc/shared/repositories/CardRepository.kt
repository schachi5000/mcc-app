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
    companion object {
        const val TAG = "CardRepository"
    }

    val cards = this.cardDatabaseDao.getCards().stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    suspend fun deleteAllCardData() {
        this.cardDatabaseDao.wipeCardTable()
    }

    suspend fun getCards(cardCodes: List<String>): List<Card> {
        val databaseCards = this.cardDatabaseDao.getCardsByCodes(cardCodes)
        val missingCardCodes = cardCodes.filter { cardCode ->
            databaseCards.all { it.code != cardCode }
        }

        val serverCards = missingCardCodes.map {
            this.marvelCDbDataSource.getCard(it).getOrThrow().also { newCard ->
                this.cardDatabaseDao.addCard(newCard)
            }
        }

        return databaseCards + serverCards
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
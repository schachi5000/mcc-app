package net.schacher.mcc.shared.datasource.database

import kotlinx.coroutines.flow.Flow
import net.schacher.mcc.shared.model.Card

interface CardDatabaseDao {
    fun getCards(): Flow<List<Card>>

    suspend fun addCards(cards: List<Card>)

    suspend fun addCard(card: Card)

    suspend fun getCardsByCodes(cardCodes: List<String>): List<Card>

    suspend fun getCardByCode(cardCode: String): Card?

    suspend fun wipeCardTable()
}
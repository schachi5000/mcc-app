package net.schacher.mcc.shared.datasource.database

import kotlinx.coroutines.flow.SharedFlow
import net.schacher.mcc.shared.model.Card

interface CardDatabaseDao {
    val onCardsUpdated: SharedFlow<List<Card>>

    suspend fun addCards(cards: List<Card>)

    suspend fun addCard(card: Card)

    suspend fun getCardByCode(cardCode: String): Card?

    suspend fun getAllCards(): List<Card>

    suspend fun wipeCardTable()
}
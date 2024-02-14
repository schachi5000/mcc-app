package net.schacher.mcc.shared.datasource.database

import net.schacher.mcc.shared.model.Card

interface CardDatabaseDao {
    suspend fun addCards(cards: List<Card>)

    suspend fun addCard(card: Card)

    suspend fun getCardByCode(cardCode: String): Card?

    suspend fun getAllCards(): List<Card>

    suspend fun wipeCardTable()
}
package net.schacher.mcc.shared.database

import net.schacher.mcc.shared.model.Card

interface CardDatabaseDao {
    fun addCards(cards: List<Card>)

    fun addCard(card: Card)

    fun getCardByCode(cardCode: String): Card?

    fun getAllCards(): List<Card>

    fun removeAllCards()
}
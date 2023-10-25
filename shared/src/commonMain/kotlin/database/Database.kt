package database

import co.touchlab.kermit.Logger
import model.Card
import net.schacher.mcc.database.CardDatabase


class CardDatabaseDao(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = CardDatabase(databaseDriverFactory.createDriver())

    private val dbQuery = database.cardDatabaseQueries

    fun addCards(cards: List<Card>) {
        Logger.d { "Adding ${cards.size} cards to database" }
        cards.forEach { this.addCard(it) }
    }

    fun addCard(card: Card) {
        this.dbQuery.addCard(
            card.code,
            card.position.toLong(),
            card.type,
            card.name,
            card.imagePath,
            card.linkedCard?.code
        )
    }

    fun getAllCards(): List<Card> = this.dbQuery.selectAllCards()
        .executeAsList()
        .map {
            val card = it.toCard()
            val linkedCard = it.linkedCardCode?.let {
                dbQuery.selectCardByCode(it).executeAsList().firstOrNull()?.toCard()
            }

            card.copy(
                linkedCard = linkedCard?.copy(
                    linkedCard = card
                )
            )
        }
}

private fun netschachermccdatabase.Card.toCard(): Card {
    return Card(
        code = code,
        position = position.toInt(),
        type = type,
        name = name,
        imagePath = imagePath,
        linkedCard = null
    )
}
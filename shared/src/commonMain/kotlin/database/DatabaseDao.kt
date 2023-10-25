package database

import co.touchlab.kermit.Logger
import model.Card

class DatabaseDao(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())

    private val dbQuery = database.appDatabaseQueries

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

private fun database.Card.toCard() = Card(
    code = code,
    position = position.toInt(),
    type = type,
    name = name,
    imagePath = imagePath,
    linkedCard = null
)
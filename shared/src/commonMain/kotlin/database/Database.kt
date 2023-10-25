package database

import co.touchlab.kermit.Logger
import model.Card
import net.schacher.mcc.database.CardDatabase


class CardDatabaseDao(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = CardDatabase(databaseDriverFactory.createDriver())

    private val dbQuery = database.cardDatabaseQueries

    fun addCard(card: Card) {
        Logger.d { "Adding card ${card.name} to database" }
        this.dbQuery.addCard(
            card.code,
            card.position.toLong(),
            card.type,
            card.name,
            card.imagePath,
            card.linkedCard?.code
        )
    }

    fun getAllCards(): List<Card> = this.dbQuery.selectAllCards().executeAsList().map {
        val card = Card(
            code = it.code,
            position = it.position.toInt(),
            type = it.type,
            name = it.name,
            imagePath = it.imagePath,
            linkedCard = null
        )

        val linkedCard = if (!it.linkedCardCode.isNullOrEmpty()) {
            dbQuery.selectCardByCode(it.linkedCardCode).executeAsList().firstOrNull()?.let {
                Card(
                    code = it.code,
                    position = it.position.toInt(),
                    type = it.type,
                    name = it.name,
                    imagePath = it.imagePath,
                    linkedCard = card
                )
            }
        } else {
            null
        }

        card.copy(
            linkedCard = linkedCard
        )
    }
}
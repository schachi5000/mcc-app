package net.schacher.mcc.shared.database

import co.touchlab.kermit.Logger
import database.AppDatabase
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck

class DatabaseDao(databaseDriverFactory: DatabaseDriverFactory) {

    private companion object {
        const val LIST_DELIMITER = ";"
    }

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

    fun getCardByCode(cardCode: String): Card = this.dbQuery.selectCardByCode(cardCode).executeAsOne().toCard()

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

    fun removeAllCards() {
        Logger.d { "Deleting all cards from database" }
        this.dbQuery.removeAllCards()
    }

    fun addDeck(deck: Deck) {
        Logger.d { "Adding deck ${deck.name} to database" }
        this.dbQuery.addDeck(
            deck.id.toLong(),
            deck.name,
            deck.heroCard.code,
            deck.cards.joinToString(LIST_DELIMITER) { it.code })
    }

    fun getDecks(): List<Deck> = this.dbQuery.selectAllDecks().executeAsList().map {
        val cards = it.cardCodes.split(LIST_DELIMITER).map {
            this.dbQuery.selectCardByCode(it).executeAsOne().toCard()
        }

        Deck(
            id = it.id.toInt(),
            name = it.name,
            heroCard = this.getCardByCode(it.heroCardCode),
            cards = cards
        )
    }

    fun removeDeck(deckId: Int) {
        Logger.d { "Deleting deck $deckId from database" }
        this.dbQuery.removeDeckById(deckId.toLong())
    }

    fun removeAllDecks() {
        Logger.d { "Deleting all decks from database" }
        this.dbQuery.removeAllDecks()
    }
}

private fun database.Card.toCard() = Card(
    code = this.code,
    position = this.position.toInt(),
    type = this.type,
    name = this.name,
    imagePath = this.imagePath,
    linkedCard = null
)
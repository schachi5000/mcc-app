package net.schacher.mcc.shared.datasource.database

import co.touchlab.kermit.Logger
import database.AppDatabase
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck

class DatabaseDao(databaseDriverFactory: DatabaseDriverFactory) : DeckDatabaseDao, CardDatabaseDao {

    private companion object {
        const val LIST_DELIMITER = ";"
    }

    private val database = AppDatabase(databaseDriverFactory.createDriver())

    private val dbQuery = database.appDatabaseQueries

    override fun addCards(cards: List<Card>) {
        Logger.d { "Adding ${cards.size} cards to database" }
        cards.forEach { this.addCard(it) }
    }

    override fun addCard(card: Card) {
        this.dbQuery.addCard(
            code = card.code,
            position = card.position.toLong(),
            type = card.type,
            packCode = card.packCode,
            name = card.name,
            imagePath = card.imagePath,
            linkedCardCode = card.linkedCard?.code
        )
    }

    override fun getCardByCode(cardCode: String): Card = this.dbQuery.selectCardByCode(cardCode).executeAsOne().toCard()

    override fun getAllCards(): List<Card> = this.dbQuery.selectAllCards()
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

    override fun removeAllCards() {
        Logger.d { "Deleting all cards from database" }
        this.dbQuery.removeAllCards()
    }

    override fun addDeck(deck: Deck) {
        Logger.d { "Adding deck ${deck.name} to database" }
        this.dbQuery.addDeck(
            deck.id.toLong(),
            deck.name,
            deck.aspect?.name,
            deck.heroCard.code,
            deck.cards.joinToString(LIST_DELIMITER) { it.code })
    }

    override fun getDecks(): List<Deck> = this.dbQuery.selectAllDecks().executeAsList().map {
        val cards = it.cardCodes.split(LIST_DELIMITER).map {
            this.dbQuery.selectCardByCode(it).executeAsOne().toCard()
        }

        Deck(
            id = it.id.toInt(),
            name = it.name,
            heroCard = this.getCardByCode(it.heroCardCode),
            aspect = it.aspect?.let { Aspect.valueOf(it) },
            cards = cards
        )
    }

    override fun removeDeck(deckId: Int) {
        Logger.d { "Deleting deck $deckId from database" }
        this.dbQuery.removeDeckById(deckId.toLong())
    }

    override fun removeAllDecks() {
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
    packCode = this.packCode,
    linkedCard = null
)
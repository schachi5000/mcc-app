package net.schacher.mcc.shared.database

import co.touchlab.kermit.Logger
import database.AppDatabase
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.model.Faction
import net.schacher.mcc.shared.model.Pack

private const val LIST_DELIMITER = ";"

class DatabaseDao(databaseDriverFactory: DatabaseDriverFactory, wipeDatabase: Boolean = false) :
    DeckDatabaseDao,
    CardDatabaseDao,
    PackDatabaseDao,
    SettingsDao {

    private val database = AppDatabase(databaseDriverFactory.createDriver())

    private val dbQuery = database.appDatabaseQueries

    init {
        if (wipeDatabase) {
            this.removeAllCards()
            this.removeAllDecks()
        }
    }

    override fun addCards(cards: List<Card>) {
        Logger.d { "Adding ${cards.size} cards to database" }
        cards.forEach { this.addCard(it) }
    }

    override fun addCard(card: Card) {
        this.dbQuery.addCard(
            code = card.code,
            position = card.position.toLong(),
            type = card.type?.name,
            packCode = card.packCode,
            packName = card.packName,
            name = card.name,
            cost = card.cost?.toLong(),
            aspect = card.aspect?.name,
            text = card.text,
            boostText = card.boostText,
            attackText = card.attackText,
            quote = card.quote,
            traits = card.traits,
            imagePath = card.imagePath,
            faction = card.faction.name,
            linkedCardCode = card.linkedCard?.code
        )
    }

    override fun getCardByCode(cardCode: String): Card =
        this.dbQuery.selectCardByCode(cardCode).executeAsOne().toCard()

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
            deck.hero.code,
            deck.cards.toCardCodeString()
        )
    }

    override fun getDecks(): List<Deck> = this.dbQuery.selectAllDecks().executeAsList().map {
        val cards = it.cardCodes.toCardCodeList().map {
            this.getCardByCode(it)
        }

        Deck(
            id = it.id.toInt(),
            name = it.name,
            hero = this.getCardByCode(it.heroCardCode),
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

    override fun getString(key: String): String? =
        this.dbQuery.getSetting(key).executeAsOneOrNull()?.value_

    override fun putString(key: String, value: String): Boolean = runCatching {
        this.dbQuery.addSetting(key, value)
    }.isSuccess

    override fun getBoolean(key: String): Boolean? =
        this.dbQuery.getSetting(key).executeAsOneOrNull()?.value_?.toBooleanStrictOrNull()

    override fun putBoolean(key: String, value: Boolean): Boolean = runCatching {
        this.dbQuery.addSetting(key, value.toString())
    }.isSuccess

    override fun remove(key: String): Boolean = runCatching {
        this.dbQuery.removeSetting(key)
    }.isSuccess

    override fun getAllEntries(): List<Pair<String, Any>> =
        this.dbQuery.getAllSettings().executeAsList().map { it.key to it.value_ }

    override fun addPack(pack: Pack) {
        Logger.d { "Adding pack ${pack.name} to database" }
        val storedPack = this.dbQuery.getPack(pack.code).executeAsOneOrNull()

        this.dbQuery.addPack(
            pack.code,
            pack.id.toLong(),
            pack.name,
            pack.position.toLong(),
            pack.cards.toCardCodeString(),
            pack.url,
            storedPack?.inPosession
        )
    }

    override fun addPacks(packs: List<Pack>) {
        Logger.d { "Adding ${packs.size} packs to database" }
        packs.forEach { this.addPack(it) }
    }

    override fun getPack(packCode: String): Pack = this.dbQuery.getPack(packCode)
        .executeAsOne()
        .toPack { cardCode -> this.getCardByCode(cardCode) }


    override fun getAllPacks(): List<Pack> =
        this.dbQuery.getAllPacks().executeAsList().map {
            it.toPack { cardCode -> this.getCardByCode(cardCode) }
        }

    override fun addPackToCollection(packCode: String) {
        val pack = this.getPack(packCode)

        this.dbQuery.addPack(
            pack.code,
            pack.id.toLong(),
            pack.name,
            pack.position.toLong(),
            pack.cards.toCardCodeString(),
            pack.url,
            true.toLong()
        )
    }

    override fun removePackToCollection(packCode: String) {
        val pack = this.getPack(packCode)

        this.dbQuery.addPack(
            pack.code,
            pack.id.toLong(),
            pack.name,
            pack.position.toLong(),
            pack.cards.toCardCodeString(),
            pack.url,
            false.toLong()
        )
    }

    override fun getPacksInCollection(): List<String> = this.dbQuery.getAllPacks()
        .executeAsList()
        .filter { it.inPosession.toBoolean() }
        .map {
            it.code
        }

    override fun hasPackInCollection(packCode: String): Boolean =
        this.dbQuery.getPack(packCode).executeAsOneOrNull()?.inPosession.toBoolean()
}

private fun String.toCardCodeList() = this.split(LIST_DELIMITER)

private fun List<Card>.toCardCodeString() = this.joinToString(LIST_DELIMITER) { it.code }

private fun Boolean.toLong() = if (this) 1L else 0L
private fun Long?.toBoolean() = this != 0L

private fun database.Pack.toPack(cardProvider: (String) -> Card) = Pack(
    name = this.name,
    code = this.code,
    cards = this.cardCodes.toCardCodeList().map {
        cardProvider(it)
    },
    url = this.url,
    id = this.id.toInt(),
    position = this.position.toInt()
)

private fun database.Card.toCard() = Card(
    code = this.code,
    position = this.position.toInt(),
    type = this.type?.let { CardType.valueOf(it) },
    name = this.name,
    text = this.text,
    boostText = this.boostText,
    attackText = this.attackText,
    quote = this.quote,
    cost = this.cost?.toInt(),
    imagePath = this.imagePath,
    aspect = this.aspect?.let { Aspect.valueOf(it) },
    packCode = this.packCode,
    packName = this.packName,
    linkedCard = null,
    traits = this.traits,
    faction = Faction.valueOf(this.faction)
)
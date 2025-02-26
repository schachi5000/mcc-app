package net.schacher.mcc.shared.datasource.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.schacher.mcc.shared.AppLogger
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType
import net.schacher.mcc.shared.model.Faction
import net.schacher.mcc.shared.model.Pack
import net.schacher.mcc.shared.utils.measuringWithContext

class DatabaseDao(
    databaseDriverFactory: DatabaseDriverFactory,
    wipeDatabase: Boolean = false,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : CardDatabaseDao, PackDatabaseDao, SettingsDao {

    private companion object {
        const val TAG = "DatabaseDao"
    }

    private val database = AppDatabase(databaseDriverFactory.createDriver())

    private val dbQuery = database.appDatabaseQueries

    init {
        if (wipeDatabase) {
            scope.launch {
                wipeCardTable()
                wipePackTable()
            }
        }
    }

    override suspend fun addCards(cards: List<Card>) =
        measuringWithContext(Dispatchers.IO, "addCards", TAG) {
            AppLogger.i { "Adding ${cards.size} cards to database" }
            cards.forEach { addCard(it) }
        }

    override suspend fun addCard(card: Card) {
        measuringWithContext(Dispatchers.IO, "addCard", TAG) {
            database.transaction {
                dbQuery.addCard(
                    code = card.code,
                    position = card.position.toLong(),
                    type = card.type?.name,
                    cardSetCode = card.setCode,
                    cardSetName = card.setName,
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
                    linkedCardCode = card.linkedCard?.code,
                    primaryColor = card.primaryColor,
                    secondaryColor = card.secondaryColor
                )

                card.linkedCard?.let {
                    dbQuery.addCard(
                        code = it.code,
                        position = it.position.toLong(),
                        type = it.type?.name,
                        cardSetCode = it.setCode,
                        cardSetName = it.setName,
                        packCode = it.packCode,
                        packName = it.packName,
                        name = it.name,
                        cost = it.cost?.toLong(),
                        aspect = it.aspect?.name,
                        text = it.text,
                        boostText = it.boostText,
                        attackText = it.attackText,
                        quote = it.quote,
                        traits = it.traits,
                        imagePath = it.imagePath,
                        faction = it.faction.name,
                        linkedCardCode = it.linkedCard?.code,
                        primaryColor = it.primaryColor,
                        secondaryColor = it.secondaryColor
                    )
                }
            }
        }
    }

    override suspend fun getCardsByCodes(cardCodes: List<String>): List<Card> =
        measuringWithContext(Dispatchers.IO, "getCardsByCodes[${cardCodes.size}]", TAG) {
            dbQuery.selectCardsByCodes(cardCodes)
                .executeAsList()
                .map {
                    val card = it.toCard()
                    var linkedCard = it.linkedCardCode?.let {
                        dbQuery.selectCardByCode(it).executeAsOneOrNull()?.toCard()
                    }

                    linkedCard = linkedCard?.copy(
                        linkedCard = card
                    )

                    card.copy(
                        linkedCard = linkedCard
                    )
                }

        }

    override suspend fun getCardByCode(cardCode: String): Card? =
        this.getCardsByCodes(listOf(cardCode)).firstOrNull()

    override fun getCards(): Flow<List<Card>> = dbQuery.selectAllCards()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map {
            val directory = it.associateBy { it.code }
            it.map { cardEntry ->
                val card = cardEntry.toCard()
                val linkedCard = cardEntry.linkedCardCode?.let {
                    directory[it]?.toCard()
                }

                card.copy(
                    linkedCard = linkedCard?.copy(
                        linkedCard = card
                    )
                )
            }
        }

    override suspend fun wipePackTable() = withContext(Dispatchers.IO) {
        AppLogger.i { "Deleting all decks from database" }
        dbQuery.removeAllPacks()
    }

    override suspend fun wipeCardTable() = withContext(Dispatchers.IO) {
        AppLogger.i { "Deleting all cards from database" }
        dbQuery.removeAllCards()
    }

    override fun getString(key: String): String? =
        this.dbQuery.getSetting(key).executeAsOneOrNull()?.value_

    override fun putString(key: String, value: String): Boolean = runCatching {
        this.dbQuery.addSetting(key, value)
    }.isSuccess

    override fun getBoolean(key: String, default: Boolean): Boolean =
        this.dbQuery.getSetting(key).executeAsOneOrNull()?.value_?.toBooleanStrictOrNull()
            ?: default

    override fun putBoolean(key: String, value: Boolean): Boolean = runCatching {
        this.dbQuery.addSetting(key, value.toString())
    }.isSuccess

    override fun remove(key: String): Boolean = runCatching {
        this.dbQuery.removeSetting(key)
    }.isSuccess

    override fun getAllEntries(): List<Pair<String, Any>> =
        this.dbQuery.getAllSettings().executeAsList().map { it.key to it.value_ }


    override suspend fun addPackToCollection(packCode: String) =
        measuringWithContext(Dispatchers.IO, "addPackToCollection", TAG) {
            dbQuery.addPackToPossession(packCode)
        }

    override suspend fun removePackFromCollection(packCode: String) =
        measuringWithContext(Dispatchers.IO, "removePackToCollection", TAG) {
            dbQuery.removePackFromPossession(packCode)
        }

    override fun getAllPacks(): Flow<List<Pack>> = this.dbQuery.getAllPacks()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map {
            it.map { pack ->
                val cards = getCardsByPackCode(pack.code)
                pack.toPack(cards)
            }
        }

    override suspend fun addPacks(packs: List<Pack>) {
        AppLogger.i { "Adding ${packs.size} packs to database" }
        packs.forEach { this.addPack(it) }
    }

    private suspend fun addPack(pack: Pack) = withContext(Dispatchers.IO) {
        val hasPackInCollection = hasPackInCollection(pack.code)

        AppLogger.i {
            "Adding pack ${pack.name} to database - hasPackInCollection:$hasPackInCollection"
        }

        addCards(pack.cards)
        dbQuery.addPack(
            pack.code,
            pack.id.toLong(),
            pack.name,
            pack.position.toLong(),
            pack.cards.map { it.code }.toCardCodeString(),
            hasPackInCollection.toLong()
        )
    }

    private suspend fun getCardsByPackCode(packCode: String): List<Card> =
        measuringWithContext(Dispatchers.IO, "getCardsByPackCode[$packCode]", TAG) {
            dbQuery.selectCardsByPackCode(packCode)
                .executeAsList()
                .map {
                    val card = it.toCard()
                    var linkedCard = it.linkedCardCode?.let {
                        dbQuery.selectCardByCode(it).executeAsList().firstOrNull()?.toCard()
                    }

                    linkedCard = linkedCard?.copy(
                        linkedCard = card
                    )

                    card.copy(
                        linkedCard = linkedCard
                    )
                }
        }


    override fun getPacksInCollection(): Flow<List<String>> =
        dbQuery.getAllPacks()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .mapNotNull {
                it.filter { it.inPosession.toBoolean() }
                    .map { it.code }
            }

    override suspend fun hasPack(packCode: String): Boolean =
        measuringWithContext(Dispatchers.IO, "hasPack", TAG) {
            dbQuery.getPack(packCode).executeAsOneOrNull() != null
        }

    override suspend fun hasPackInCollection(packCode: String): Boolean =
        measuringWithContext(Dispatchers.IO, "hasPackInCollection", TAG) {
            dbQuery.getPack(packCode).executeAsOneOrNull()?.inPosession.toBoolean()
        }
}

private const val LIST_DELIMITER = ";"

private fun String.toCardCodeList() = this.split(LIST_DELIMITER)

private fun List<String>.toCardCodeString() = this.joinToString(LIST_DELIMITER)

private fun Boolean.toLong() = if (this) 1L else 0L

private fun Long?.toBoolean() = if (this == null) false else this != 0L

private fun database.Pack.toPack(cards: List<Card>) = Pack(
    name = this.name,
    code = this.code,
    cards = cards,
    id = this.id.toInt(),
    cardCodes = this.cardCodes.toCardCodeList(),
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
    faction = Faction.valueOf(this.faction),
    setCode = this.cardSetCode,
    setName = this.cardSetName,
    primaryColor = this.primaryColor,
    secondaryColor = this.secondaryColor
)
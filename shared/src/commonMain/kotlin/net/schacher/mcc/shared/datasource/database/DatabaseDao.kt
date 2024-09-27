package net.schacher.mcc.shared.datasource.database

import co.touchlab.kermit.Logger
import database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType
import net.schacher.mcc.shared.model.Faction
import net.schacher.mcc.shared.model.Pack
import net.schacher.mcc.shared.utils.measuringWithContext

private const val LIST_DELIMITER = ";"

class DatabaseDao(
    databaseDriverFactory: DatabaseDriverFactory,
    wipeDatabase: Boolean = false,
    scope: CoroutineScope = MainScope()
) :
    CardDatabaseDao,
    PackDatabaseDao,
    SettingsDao {

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
        measuringWithContext(Dispatchers.IO, "addCards") {
            Logger.i { "Adding ${cards.size} cards to database" }
            cards.forEach { addCard(it) }
        }

    override suspend fun addCard(card: Card) = measuringWithContext(Dispatchers.IO, "addCard") {
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
            linkedCardCode = card.linkedCard?.code
        )
    }

    override suspend fun getCardByCode(cardCode: String): Card? =
        measuringWithContext(Dispatchers.IO, "getCardByCode") {
            dbQuery.selectCardByCode(cardCode).executeAsOneOrNull()?.toCard()
        }

    override suspend fun getAllCards(): List<Card> =
        measuringWithContext(Dispatchers.IO, "getAllCards") {
            dbQuery.selectAllCards()
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

    override suspend fun wipePackTable() = withContext(Dispatchers.IO) {
        Logger.i { "Deleting all decks from database" }
        dbQuery.removeAllPacks()
    }

    override suspend fun wipeCardTable() = withContext(Dispatchers.IO) {
        Logger.i { "Deleting all cards from database" }
        dbQuery.removeAllCards()
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

    private suspend fun addPack(pack: Pack) = withContext(Dispatchers.IO) {
        Logger.i { "Adding pack ${pack.name} to database ${hasPackInCollection(pack.code)}" }

        dbQuery.addPack(
            pack.code,
            pack.id.toLong(),
            pack.name,
            pack.position.toLong(),
            pack.cardCodes.toCardCodeString(),
            pack.url,
            hasPackInCollection(pack.code).toLong()
        )
    }

    override suspend fun addPacks(packs: List<Pack>) {
        Logger.i { "Adding ${packs.size} packs to database" }
        packs.forEach { this.addPack(it) }
    }

    private suspend fun getPack(packCode: String): Pack =
        measuringWithContext(Dispatchers.IO, "getPack") {
            dbQuery.getPack(packCode)
                .executeAsOne()
                .toPack()
        }


    override suspend fun getAllPacks(): List<Pack> =
        measuringWithContext(Dispatchers.IO, "getAllPacks") {
            dbQuery.getAllPacks().executeAsList().map { it.toPack() }
        }

    override suspend fun addPackToCollection(packCode: String) =
        measuringWithContext(Dispatchers.IO, "addPackToCollection") {
            val pack = getPack(packCode)

            dbQuery.addPack(
                pack.code,
                pack.id.toLong(),
                pack.name,
                pack.position.toLong(),
                pack.cardCodes.toCardCodeString(),
                pack.url,
                true.toLong()
            )
        }

    override suspend fun removePackToCollection(packCode: String) =
        measuringWithContext(Dispatchers.IO, "removePackToCollection") {
            val pack = getPack(packCode)

            dbQuery.addPack(
                pack.code,
                pack.id.toLong(),
                pack.name,
                pack.position.toLong(),
                pack.cardCodes.toCardCodeString(),
                pack.url,
                false.toLong()
            )
        }

    override suspend fun getPacksInCollection(): List<String> =
        measuringWithContext(Dispatchers.IO, "getPacksInCollection") {
            dbQuery.getAllPacks()
                .executeAsList()
                .filter { it.inPosession.toBoolean() }
                .map {
                    it.code
                }
        }

    override suspend fun hasPackInCollection(packCode: String): Boolean =
        measuringWithContext(Dispatchers.IO, "hasPackInCollection") {
            dbQuery.getPack(packCode).executeAsOneOrNull()?.inPosession.toBoolean()
        }
}

private fun String.toCardCodeList() = this.split(LIST_DELIMITER)

private fun List<String>.toCardCodeString() = this.joinToString(LIST_DELIMITER)

private fun Boolean.toLong() = if (this) 1L else 0L

private fun Long?.toBoolean() = if (this == null) false else this != 0L

private fun database.Pack.toPack() = Pack(
    name = this.name,
    code = this.code,
    cardCodes = this.cardCodes.toCardCodeList(),
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
    faction = Faction.valueOf(this.faction),
    setCode = this.cardSetCode,
    setName = this.cardSetName
)
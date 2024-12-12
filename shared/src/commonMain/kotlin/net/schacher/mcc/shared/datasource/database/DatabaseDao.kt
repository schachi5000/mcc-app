package net.schacher.mcc.shared.datasource.database

import co.touchlab.kermit.Logger
import database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _onCardAdded = MutableSharedFlow<Card>()

    override val onCardAdded = this._onCardAdded.asSharedFlow()

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
            Logger.i { "Adding ${cards.size} cards to database" }
            cards.forEach { addCard(it) }
        }

    override suspend fun addCard(card: Card) =
        measuringWithContext(Dispatchers.IO, "addCard") {
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
        }.also {
            _onCardAdded.emit(card)
        }

    override suspend fun getCardByCode(cardCode: String): Card? =
        measuringWithContext(Dispatchers.IO, "getCardByCode", TAG) {
            val card = dbQuery.selectCardByCode(cardCode).executeAsOneOrNull()?.toCard()
            var linkedCard = card?.linkedCard?.code?.let {
                dbQuery.selectCardByCode(it).executeAsList().firstOrNull()?.toCard()
            }

            linkedCard = linkedCard?.copy(
                linkedCard = card
            )

            card?.copy(
                linkedCard = linkedCard
            )
        }


    private suspend fun getCardsByPackCode(packCode: String): List<Card> =
        measuringWithContext(Dispatchers.IO, "getCardsByPackCode", TAG) {
            dbQuery.selectCardsByPackCode(packCode).executeAsList().map {
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

    override suspend fun getAllCards(): List<Card> =
        measuringWithContext(Dispatchers.IO, "getAllCards", TAG) {
            dbQuery.selectAllCards().executeAsList().map {
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

    private suspend fun addPack(pack: Pack) = withContext(Dispatchers.IO) {
        Logger.i {
            "Adding pack ${pack.name} to database - hasPackInCollection:${
                hasPackInCollection(
                    pack.code
                )
            }"
        }

        addCards(pack.cards)
        dbQuery.addPack(
            pack.code,
            pack.id.toLong(),
            pack.name,
            pack.position.toLong(),
            pack.cards.map { it.code }.toCardCodeString(),
            hasPackInCollection(pack.code).toLong()
        )
    }

    override suspend fun addPacks(packs: List<Pack>) {
        Logger.i { "Adding ${packs.size} packs to database" }
        packs.forEach {
            this.addPack(it)
        }
    }

    override suspend fun getAllPacks(): List<Pack> =
        measuringWithContext(Dispatchers.IO, "getAllPacks", TAG) {
            dbQuery.getAllPacks().executeAsList().map {
                val cards = getCardsByPackCode(it.code)
                it.toPack(cards)
            }
        }

    override suspend fun addPackToCollection(packCode: String) =
        measuringWithContext(Dispatchers.IO, "addPackToCollection", TAG) {
            dbQuery.addPackToPossession(packCode)
        }

    override suspend fun removePackFromCollection(packCode: String) =
        measuringWithContext(Dispatchers.IO, "removePackToCollection", TAG) {
            dbQuery.removePackFromPossession(packCode)
        }

    override suspend fun getPacksInCollection(): List<String> =
        measuringWithContext(Dispatchers.IO, "getPacksInCollection", TAG) {
            dbQuery.getAllPacks()
                .executeAsList()
                .filter { it.inPosession.toBoolean() }
                .map { it.code }
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
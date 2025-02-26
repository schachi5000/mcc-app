package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import net.schacher.mcc.shared.AppLogger
import net.schacher.mcc.shared.datasource.database.PackDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Card
import kotlin.time.measureTimedValue

class PackRepository(
    private val packDatabaseDao: PackDatabaseDao,
    private val marvelCDbDataSource: MarvelCDbDataSource,
    scope: CoroutineScope = MainScope()
) {
    val packs = this.packDatabaseDao.getAllPacks().stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    val packsInCollection = this.packDatabaseDao.getPacksInCollection().stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    suspend fun refreshAllPacks() {
        AppLogger.d("PackRepository") { "Refreshing all packs" }
        val packCodes = measureTimedValue {
            this.marvelCDbDataSource.getAllPackCodes().getOrNull() ?: emptyList()
        }.also {
            AppLogger.d("PackRepository") { "All pack codes loaded in ${it.duration}" }
        }.value

        val unknownPackCodes = packCodes.filter { !this.packDatabaseDao.hasPack(it) }

        this.marvelCDbDataSource.getPacks(unknownPackCodes).collect {
            AppLogger.i("PackRepository") { "Pack [${it.name}] loaded" }
            try {
                this.packDatabaseDao.addPacks(listOf(it))
            } catch (e: Exception) {
                AppLogger.e { "Error adding pack [${it.name}] to database: ${e.message}" }
            }
        }
    }

    suspend fun deleteAllPackData() {
        this.packDatabaseDao.wipePackTable()
    }

    fun hasPackInCollection(packCode: String) = this.packsInCollection.value.contains(packCode)

    fun hasCardInCollection(card: Card) =
        this.packsInCollection.value.any { it.contains(card.packCode) }

    suspend fun addPackToCollection(packCode: String) {
        this.packDatabaseDao.addPackToCollection(packCode)
    }

    suspend fun removePackFromCollection(packCode: String) {
        this.packDatabaseDao.removePackFromCollection(packCode)
    }
}
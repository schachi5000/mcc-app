package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import net.schacher.mcc.shared.AppLogger
import net.schacher.mcc.shared.datasource.database.PackDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Card

class PackRepository(
    private val packDatabaseDao: PackDatabaseDao,
    private val marvelCDbDataSource: MarvelCDbDataSource,
) {
    private companion object {
        const val TAG = "PackRepository"
    }

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val packs = this.packDatabaseDao.getAllPacks().stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val packsInCollection = this.packDatabaseDao.getPacksInCollection().stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _refreshState = MutableStateFlow(false)

    val refreshState = _refreshState.asStateFlow()

    suspend fun refreshAllPacks() {
        AppLogger.d(TAG) { "Refreshing all packs" }

        try {
            this._refreshState.value = true

            val packCodes = this.marvelCDbDataSource.getAllPackCodes().getOrNull() ?: emptyList()
            val unknownPackCodes = packCodes.filter { !this.packDatabaseDao.hasPack(it) }

            this.marvelCDbDataSource.getPacks(unknownPackCodes).collect {
                AppLogger.i(TAG) { "Pack [${it.name}] loaded" }
                try {
                    this.packDatabaseDao.addPacks(listOf(it))
                } catch (e: Exception) {
                    AppLogger.e(TAG) { "Error adding pack [${it.name}] to database: ${e.message}" }
                }
            }
        } finally {
            this._refreshState.value = false
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
package net.schacher.mcc.shared.repositories

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.datasource.database.PackDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Pack

class PackRepository(
    private val packDatabaseDao: PackDatabaseDao,
    private val marvelCDbDataSource: MarvelCDbDataSource,
    private val scope: CoroutineScope = MainScope()
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
        val newPacks = this.marvelCDbDataSource.getAllPacks().getOrThrow()

        Logger.i { "${newPacks.size} packs loaded" }
        try {
            this.packDatabaseDao.addPacks(newPacks)
        } catch (e: Exception) {
            Logger.e { "Error adding packs to database: ${e.message}" }
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
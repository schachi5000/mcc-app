package net.schacher.mcc.shared.repositories

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.schacher.mcc.shared.datasource.database.PackDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Pack

class PackRepository(
    private val packDatabaseDao: PackDatabaseDao,
    private val marvelCDbDataSource: MarvelCDbDataSource
) {
    private val _state = MutableStateFlow(this.packDatabaseDao.getAllPacks())

    val state = _state.asStateFlow()

    val allPacks: List<Pack>
        get() = _state.value

    val packsInCollectionCount: Int
        get() = runCatching {
            this.packDatabaseDao.getPacksInCollection().size
        }.getOrElse { 0 }

    suspend fun refresh() {
        val newPacks = this.marvelCDbDataSource.getAllPacks()

        Logger.i { "${newPacks.size} packs loaded" }
        try {
            this.packDatabaseDao.addPacks(newPacks)
        } catch (e: Exception) {
            Logger.e { "Error adding packs to database: ${e.message}" }
        }

        _state.update { newPacks }
    }

    suspend fun deleteAllPackData() {
        this.packDatabaseDao.wipePackTable()
        _state.update { emptyList() }
    }

    fun hasPackInCollection(packCode: String): Boolean =
        this.packDatabaseDao.hasPackInCollection(packCode)

    fun addPackToCollection(packCode: String) {
        this.packDatabaseDao.addPackToCollection(packCode)
    }

    fun removePackFromCollection(packCode: String) {
        this.packDatabaseDao.removePackToCollection(packCode)
    }
}
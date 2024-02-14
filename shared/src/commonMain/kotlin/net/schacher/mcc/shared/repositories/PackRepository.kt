package net.schacher.mcc.shared.repositories

import co.touchlab.kermit.Logger
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.datasource.database.PackDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Pack

class PackRepository(
    private val packDatabaseDao: PackDatabaseDao,
    private val marvelCDbDataSource: MarvelCDbDataSource
) {
    private val _state = MutableStateFlow<List<Pack>>(emptyList())

    val state = _state.asStateFlow()

    val allPacks: List<Pack>
        get() = _state.value

    var packsInCollectionCount: Int = 0
        private set

    init {
        MainScope().launch {
            packsInCollectionCount = packDatabaseDao.getPacksInCollection().size
            _state.emit(packDatabaseDao.getAllPacks())
        }
    }

    suspend fun refreshAllPacks() {
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

    suspend fun hasPackInCollection(packCode: String): Boolean =
        this.packDatabaseDao.hasPackInCollection(packCode)

    suspend fun addPackToCollection(packCode: String) {
        this.packDatabaseDao.addPackToCollection(packCode)
    }

    suspend fun removePackFromCollection(packCode: String) {
        this.packDatabaseDao.removePackToCollection(packCode)
    }
}
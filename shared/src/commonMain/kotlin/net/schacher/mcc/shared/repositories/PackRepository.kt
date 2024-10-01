package net.schacher.mcc.shared.repositories

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.datasource.database.PackDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Pack

class PackRepository(
    private val packDatabaseDao: PackDatabaseDao,
    private val marvelCDbDataSource: MarvelCDbDataSource,
    private val scope: CoroutineScope = MainScope()
) {
    private val _packs = MutableStateFlow<List<Pack>>(emptyList())

    val packs = this._packs.asStateFlow()

    private val _packsInCollection = MutableStateFlow<List<String>>(emptyList())

    val packsInCollection = this._packsInCollection.asStateFlow()

    init {
        this.scope.launch {
            _packsInCollection.emit(packDatabaseDao.getPacksInCollection())
            _packs.emit(packDatabaseDao.getAllPacks())
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

        _packsInCollection.emit(packDatabaseDao.getPacksInCollection())
        _packs.emit(newPacks)
    }

    suspend fun deleteAllPackData() {
        this.packDatabaseDao.wipePackTable()
        this._packsInCollection.emit(emptyList())
        this._packs.emit(emptyList())
    }

    fun hasPackInCollection(packCode: String) = this.packsInCollection.value.contains(packCode)

    suspend fun addPackToCollection(packCode: String) {
        this.packDatabaseDao.addPackToCollection(packCode)
        _packsInCollection.emit(packDatabaseDao.getPacksInCollection())
    }

    suspend fun removePackFromCollection(packCode: String) {
        this.packDatabaseDao.removePackFromCollection(packCode)
        _packsInCollection.emit(packDatabaseDao.getPacksInCollection())
    }
}
package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.schacher.mcc.shared.database.PackDatabaseDao
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
        get() = this.packDatabaseDao.getPacksInCollection().size

    suspend fun refresh() {
        val newPacks = this.marvelCDbDataSource.getAllPacks()

        this.packDatabaseDao.addPacks(newPacks)
        _state.update { newPacks }
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
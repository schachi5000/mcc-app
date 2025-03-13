package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import net.schacher.mcc.shared.datasource.database.PackDatabaseDao
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Pack

class PackRepository(private val packDatabaseDao: PackDatabaseDao) {
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

    fun hasPackInCollection(packCode: String) = this.packsInCollection.value.contains(packCode)

    fun hasCardInCollection(card: Card) =
        this.packsInCollection.value.any { it.contains(card.packCode) }

    fun hasPack(packCode: String): Boolean {
        return this.packs.value.any { it.code == packCode }
    }

    suspend fun addPackToCollection(packCode: String) {
        this.packDatabaseDao.addPackToCollection(packCode)
    }

    suspend fun removePackFromCollection(packCode: String) {
        this.packDatabaseDao.removePackFromCollection(packCode)
    }

    suspend fun deleteAllPackData() {
        this.packDatabaseDao.wipePackTable()
    }

    suspend fun addPacks(packs: List<Pack>) {
        this.packDatabaseDao.addPacks(packs)
    }
}
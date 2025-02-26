package net.schacher.mcc.shared.datasource.database

import kotlinx.coroutines.flow.Flow
import net.schacher.mcc.shared.model.Pack

interface PackDatabaseDao {
    fun getAllPacks(): Flow<List<Pack>>

    fun getPacksInCollection(): Flow<List<String>>

    suspend fun addPacks(packs: List<Pack>)

    suspend fun addPackToCollection(packCode: String)

    suspend fun removePackFromCollection(packCode: String)

    suspend fun hasPack(packCode: String): Boolean

    suspend fun hasPackInCollection(packCode: String): Boolean

    suspend fun wipePackTable()
}

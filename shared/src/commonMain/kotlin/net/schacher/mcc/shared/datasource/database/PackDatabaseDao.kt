package net.schacher.mcc.shared.datasource.database

import net.schacher.mcc.shared.model.Pack

interface PackDatabaseDao {
    suspend fun addPacks(packs: List<Pack>)

    suspend fun getAllPacks(): List<Pack>

    suspend fun addPackToCollection(packCode: String)

    suspend fun removePackToCollection(packCode: String)

    suspend fun getPacksInCollection(): List<String>

    suspend fun hasPackInCollection(packCode: String): Boolean

    suspend fun wipePackTable()
}

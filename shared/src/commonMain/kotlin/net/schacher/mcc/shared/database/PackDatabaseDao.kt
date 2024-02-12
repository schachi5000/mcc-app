package net.schacher.mcc.shared.database

import net.schacher.mcc.shared.model.Pack

interface PackDatabaseDao {
    fun addPack(pack: Pack)

    fun addPacks(packs: List<Pack>)

    fun getPack(packCode: String): Pack

    fun getAllPacks(): List<Pack>

    fun addPackToCollection(packCode: String)

    fun removePackToCollection(packCode: String)

    fun getPacksInCollection(): List<String>

    fun hasPackInCollection(packCode: String): Boolean
    fun removeAllPacks()
}

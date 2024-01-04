package net.schacher.mcc.shared.database

interface SettingsDao {

    fun getString(key: String): String?

    fun putString(key: String, value: String): Boolean

    fun getBoolean(key: String): Boolean?

    fun putBoolean(key: String, value: Boolean): Boolean

    fun remove(key: String): Boolean

    fun getAllEntries(): List<Pair<String, Any>>
}
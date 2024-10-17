package net.schacher.mcc.shared.datasource.database

interface SettingsDao {

    fun getString(key: String): String?

    fun putString(key: String, value: String): Boolean

    fun getBoolean(key: String, default: Boolean): Boolean

    fun putBoolean(key: String, value: Boolean): Boolean

    fun remove(key: String): Boolean

    fun getAllEntries(): List<Pair<String, Any>>
}
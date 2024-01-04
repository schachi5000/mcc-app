package net.schacher.mcc.shared.database

import com.squareup.sqldelight.db.SqlDriver

internal const val DATABASE_NAME = "app.db"

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
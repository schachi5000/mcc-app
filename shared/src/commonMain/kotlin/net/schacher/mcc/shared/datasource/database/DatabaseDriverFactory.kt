package net.schacher.mcc.shared.datasource.database

import app.cash.sqldelight.db.SqlDriver

internal const val DATABASE_NAME = "app.db"

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
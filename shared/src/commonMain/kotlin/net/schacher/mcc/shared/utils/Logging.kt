package net.schacher.mcc.shared.utils

import co.touchlab.kermit.Logger
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

fun Logger.Companion.debug(message: () -> String) {
    i("DEBUG") { message() }
}

fun <T> measureAndLogDuration(label: String? = null, block: () -> T): T = measureTimedValue { block() }
    .also { Logger.i { "$label took ${it.duration.toString(DurationUnit.MILLISECONDS)}" } }
    .value
package net.schacher.mcc.shared.utils

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

fun Logger.Companion.debug(message: () -> String) {
    i("DEBUG") { message() }
}

fun <T> measureAndLog(label: String? = null, block: () -> T): T = measureTimedValue { block() }
    .also { logDuration(label, it.duration) }
    .value

suspend fun <T> measuringWithContext(
    dispatcher: CoroutineDispatcher, label: String? = null, block: suspend () -> T
): T = measureTimedValue { withContext(dispatcher) { block() } }
    .also { logDuration(label, it.duration) }
    .value

private fun logDuration(label: String?, duration: kotlin.time.Duration) {
    Logger.i { "[$label] took ${duration.toString(DurationUnit.MILLISECONDS, 2)}" }
}
package net.schacher.mcc.shared.utils

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.schacher.mcc.shared.AppLogger
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

fun Logger.Companion.debug(message: () -> String) {
    i("DEBUG") { message() }
}

fun AppLogger.debug(message: () -> String) {
    i("DEBUG") { message() }
}

fun Logger.Companion.e(throwable: Throwable) {
    e(throwable.toString(), throwable)
}

fun <T> measureAndLog(label: String? = null, block: () -> T): T = measureTimedValue { block() }
    .also { logDuration(label, it.duration) }
    .value


suspend fun <T> measuringWithContext(
    dispatcher: CoroutineDispatcher,
    label: String? = null,
    tag: String? = null,
    block: suspend () -> T
): T = measureTimedValue { withContext(dispatcher) { block() } }
    .also { logDuration(label, it.duration, tag) }
    .value

private fun logDuration(label: String?, duration: kotlin.time.Duration, tag: String? = null) {
    val message = "[$label] took ${duration.toString(DurationUnit.MILLISECONDS, 2)}"
    if (tag != null) {
        AppLogger.i(tag) { message }
    } else {
        AppLogger.i { message }
    }
}
package net.schacher.mcc.shared.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Convenience function to launch a coroutine in a given [CoroutineScope]
 * and collect the values from the given [StateFlow].
 */
fun <T> CoroutineScope.launchAndCollect(
    flow: StateFlow<T>,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: (T) -> Unit
): Job = this.launch(context, start) {
    flow.collect {
        block(it)
    }
}
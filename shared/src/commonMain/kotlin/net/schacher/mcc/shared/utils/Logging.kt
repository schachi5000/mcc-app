package net.schacher.mcc.shared.utils

import co.touchlab.kermit.Logger

fun Logger.Companion.debug(message: () -> String) {
    d("DEBUG") { message() }
}
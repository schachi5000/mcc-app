package net.schacher.mcc.shared

import co.touchlab.kermit.Logger

object TestClass {

    fun test(text: String) {
        Logger.i { text }
    }
}
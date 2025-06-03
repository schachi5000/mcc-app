package net.schacher.mcc.shared

import co.touchlab.kermit.Logger

object AppLogger {

    fun d(tag: String? = null, msg: () -> String) {
        if (tag != null) {
            Logger.i(tag) { msg() }
        } else {
            Logger.i { msg() }
        }
    }

    fun v(tag: String? = null, msg: () -> String) {
        if (tag != null) {
            Logger.i(tag) { msg() }
        } else {
            Logger.i { msg() }
        }
    }

    fun i(tag: String? = null, msg: () -> String) {
        if (tag != null) {
            Logger.i(tag) { msg() }
        } else {
            Logger.i { msg() }
        }
    }

    fun w(tag: String? = null, msg: () -> String) {
        if (tag != null) {
            Logger.i(tag) { msg() }
        } else {
            Logger.i { msg() }
        }
    }

    fun e(tag: String? = null, msg: (() -> String)? = null) {
        Logger.e(tag = tag ?: "", messageString = msg?.invoke() ?: "No message")
    }

    fun e(throwable: Throwable, tag: String? = null, msg: (() -> String)? = null) {
        if (tag != null) {
            Logger.e(
                tag = tag,
                throwable = throwable,
                messageString = msg?.invoke() ?: throwable.message ?: "No message"
            )
        } else {
            Logger.e(
                throwable = throwable,
                messageString = msg?.invoke() ?: throwable.message ?: "No message"
            )
        }
    }
}
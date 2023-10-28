package net.schacher.mcc.shared.time

actual object Time {
    actual val currentTimeMillis: Long
        get() = System.currentTimeMillis()
}
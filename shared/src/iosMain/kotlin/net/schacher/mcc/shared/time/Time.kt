package net.schacher.mcc.shared.time

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual object Time {
    actual val currentTimeMillis: Long
        get() = (NSDate().timeIntervalSince1970 * 1000).toLong()

}
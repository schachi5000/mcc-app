package net.schacher.mcc.shared.platform

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSBundle

actual val platformModule = module {
    factoryOf(::IOsPlatformInfo) bind PlatformInfo::class
}

class IOsPlatformInfo : PlatformInfo {
    override val platform: Platform
        get() = Platform.IOS
    override val version: String
        get() = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String
            ?: "Unknown"

}
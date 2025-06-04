package net.schacher.mcc.shared.platform

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSBundle
import kotlin.experimental.ExperimentalNativeApi

actual val platformModule = module {
    factoryOf(::IosPlatformInfo) bind PlatformInfo::class
}

class IosPlatformInfo : PlatformInfo {
    override val platform: Platform
        get() = Platform.IOS
    override val version: String
        get() = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String
            ?: "Unknown"

    @OptIn(ExperimentalNativeApi::class)
    override val debugBuild: Boolean
        get() = kotlin.native.Platform.isDebugBinary
}
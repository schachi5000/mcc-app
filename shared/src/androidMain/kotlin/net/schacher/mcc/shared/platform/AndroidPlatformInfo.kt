package net.schacher.mcc.shared.platform

import android.content.Context
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    factoryOf(::AndroidPlatformInfo) bind PlatformInfo::class
}

class AndroidPlatformInfo(context: Context) : PlatformInfo {

    override val platform: Platform = Platform.ANDROID

    override val version: String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "unknown"

    override fun toString(): String {
        return "AndroidPlatformInfo(platform=$platform, version='$version')"
    }
}
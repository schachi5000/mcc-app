package net.schacher.mcc.shared.platform

import androidx.compose.runtime.Composable
import org.koin.compose.koinInject
import org.koin.core.module.Module

expect val platformModule: Module

enum class Platform {
    ANDROID, IOS;
}

interface PlatformInfo {
    val platform: Platform
    val version: String
}

@Composable
fun isIOs(platformInfo: PlatformInfo = koinInject()): Boolean {
    return platformInfo.platform == Platform.IOS
}

@Composable
fun isAndroid(platformInfo: PlatformInfo = koinInject()): Boolean {
    return platformInfo.platform == Platform.ANDROID
}
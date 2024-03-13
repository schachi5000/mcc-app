import androidx.compose.ui.window.ComposeUIViewController
import net.schacher.mcc.shared.datasource.database.DatabaseDao
import net.schacher.mcc.shared.datasource.database.DatabaseDriverFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSBundle

actual val platform: Platform = Platform.IOS

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

fun MainViewController() = ComposeUIViewController { App(DatabaseDao(DatabaseDriverFactory())) }
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import co.touchlab.kermit.Logger
import net.schacher.mcc.shared.datasource.database.DatabaseDao
import net.schacher.mcc.shared.datasource.database.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platform: Platform = Platform.ANDROID


actual val platformModule = module {
    factoryOf(::AndroidPlatformInfo) bind PlatformInfo::class
}

class AndroidPlatformInfo(context: Context) : PlatformInfo {
    
    override val platform: Platform = Platform.ANDROID

    override val version: String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName

    override fun toString(): String {
        return "AndroidPlatformInfo(platform=$platform, version='$version')"
    }

    init {
        Logger.d { "$this" }
    }
}

@Composable
fun MainView() {
    val context = LocalContext.current
    App(DatabaseDao(DatabaseDriverFactory(LocalContext.current))) {
        androidContext(context)
    }
}
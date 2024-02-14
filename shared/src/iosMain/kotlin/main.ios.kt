import androidx.compose.ui.window.ComposeUIViewController
import net.schacher.mcc.shared.datasource.database.DatabaseDao
import net.schacher.mcc.shared.datasource.database.DatabaseDriverFactory

actual val platform: Platform = Platform.IOS

fun MainViewController() = ComposeUIViewController { App(DatabaseDao(DatabaseDriverFactory())) }
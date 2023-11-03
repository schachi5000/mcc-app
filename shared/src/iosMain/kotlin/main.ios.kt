import androidx.compose.ui.window.ComposeUIViewController
import net.schacher.mcc.shared.datasource.database.DatabaseDao
import net.schacher.mcc.shared.datasource.database.DatabaseDriverFactory

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController { App(DatabaseDao(DatabaseDriverFactory())) }
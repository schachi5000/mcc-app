import androidx.compose.ui.window.ComposeUIViewController
import net.schacher.mcc.shared.database.DatabaseDao
import net.schacher.mcc.shared.database.DatabaseDriverFactory

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController { App(DatabaseDao(DatabaseDriverFactory())) }
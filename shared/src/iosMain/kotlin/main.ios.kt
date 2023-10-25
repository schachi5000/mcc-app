import androidx.compose.ui.window.ComposeUIViewController
import database.DatabaseDao
import database.DatabaseDriverFactory

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController { App(DatabaseDao(DatabaseDriverFactory())) }
import androidx.compose.ui.window.ComposeUIViewController
import database.CardDatabaseDao
import database.DatabaseDriverFactory

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController { App(CardDatabaseDao(DatabaseDriverFactory())) }
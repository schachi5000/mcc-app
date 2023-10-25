import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import database.CardDatabaseDao
import database.DatabaseDriverFactory

actual fun getPlatformName(): String = "Android"

@Composable fun MainView() {
    App(CardDatabaseDao(DatabaseDriverFactory(LocalContext.current)))
}

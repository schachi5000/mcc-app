import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import database.DatabaseDao
import database.DatabaseDriverFactory

actual fun getPlatformName(): String = "Android"

@Composable fun MainView() {
    App(DatabaseDao(DatabaseDriverFactory(LocalContext.current)))
}

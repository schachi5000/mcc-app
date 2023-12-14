import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import net.schacher.mcc.shared.datasource.database.DatabaseDao
import net.schacher.mcc.shared.datasource.database.DatabaseDriverFactory

@Composable
fun MainView() {
    App(DatabaseDao(DatabaseDriverFactory(LocalContext.current)))
}
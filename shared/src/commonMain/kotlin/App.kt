import androidx.compose.runtime.Composable
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import net.schacher.mcc.shared.datasource.database.DatabaseDao
import net.schacher.mcc.shared.design.theme.MccTheme
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.screens.main.MainScreen
import net.schacher.mcc.shared.screens.main.MainViewModel

@Composable
fun App(databaseDao: DatabaseDao) {
    val cardRepository = CardRepository(databaseDao)
    val deckRepository = DeckRepository(cardRepository, databaseDao)
    val mainViewModel = getViewModel(Unit, viewModelFactory { MainViewModel(cardRepository) })

    MccTheme {
        MainScreen(mainViewModel, cardRepository, deckRepository)
    }
}
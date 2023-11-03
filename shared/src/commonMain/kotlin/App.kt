import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import net.schacher.mcc.shared.datasource.database.DatabaseDao
import net.schacher.mcc.shared.design.theme.DarkColorScheme
import net.schacher.mcc.shared.design.theme.LightColorScheme
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.screens.main.MainScreen
import net.schacher.mcc.shared.screens.main.MainViewModel

@Composable
fun App(databaseDao: DatabaseDao) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    ) {
        val cardRepository = CardRepository(databaseDao)
        val deckRepository = DeckRepository(cardRepository, databaseDao)

        val mainViewModel = getViewModel(Unit, viewModelFactory { MainViewModel(cardRepository) })

        MainScreen(mainViewModel, cardRepository, deckRepository)
    }
}
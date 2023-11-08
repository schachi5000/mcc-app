import androidx.compose.runtime.Composable
import net.schacher.mcc.shared.datasource.database.DatabaseDao
import net.schacher.mcc.shared.design.theme.MccTheme
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.screens.deck.DeckViewModel
import net.schacher.mcc.shared.screens.featured.FeaturedViewModel
import net.schacher.mcc.shared.screens.main.MainScreen
import net.schacher.mcc.shared.screens.main.MainViewModel
import net.schacher.mcc.shared.screens.search.SearchViewModel
import net.schacher.mcc.shared.screens.settings.SettingsViewModel
import org.koin.compose.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.module


val repositories: Module = module {
    single { CardRepository(get()) }
    single { DeckRepository(get(), get()) }
}

val viewModels: Module = module {
    single { MainViewModel(get(), get()) }
    single { DeckViewModel(get(), get()) }
    single { SettingsViewModel(get(), get()) }
    single { SearchViewModel(get()) }
    single { FeaturedViewModel(get()) }
}

@Composable
fun App(databaseDao: DatabaseDao) {
    KoinApplication(application = {
        modules(
            module { single { databaseDao } },
            repositories,
            viewModels
        )
    }) {
        MccTheme {
            MainScreen()
        }
    }
}

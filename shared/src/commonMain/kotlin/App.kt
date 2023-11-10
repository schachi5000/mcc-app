import androidx.compose.runtime.Composable
import net.schacher.mcc.shared.datasource.database.DatabaseDao
import net.schacher.mcc.shared.datasource.http.KtorMarvelCDbDataSource
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
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
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val network = module {
    singleOf<MarvelCDbDataSource>(::KtorMarvelCDbDataSource)
}

val repositories = module {
    singleOf(::CardRepository)
    singleOf(::DeckRepository)
}

val viewModels = module {
    singleOf(::MainViewModel)
    singleOf(::DeckViewModel)
    singleOf(::SettingsViewModel)
    singleOf(::SearchViewModel)
    singleOf(::FeaturedViewModel)
}

@Composable
fun App(databaseDao: DatabaseDao) {
    KoinApplication(application = {
        modules(
            module { single { databaseDao } },
            network,
            repositories,
            viewModels
        )
    }) {
        MccTheme {
            MainScreen()
        }
    }
}
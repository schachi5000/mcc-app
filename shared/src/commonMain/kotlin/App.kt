import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import net.schacher.mcc.shared.auth.AuthHandler
import net.schacher.mcc.shared.datasource.database.CardDatabaseDao
import net.schacher.mcc.shared.datasource.database.DatabaseDao
import net.schacher.mcc.shared.datasource.database.DeckDatabaseDao
import net.schacher.mcc.shared.datasource.database.PackDatabaseDao
import net.schacher.mcc.shared.datasource.database.SettingsDao
import net.schacher.mcc.shared.datasource.http.KtorMarvelCDbDataSource
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.design.theme.MccTheme
import net.schacher.mcc.shared.platform.platformModule
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.repositories.PackRepository
import net.schacher.mcc.shared.screens.login.LoginScreen
import net.schacher.mcc.shared.screens.main.MainScreen
import net.schacher.mcc.shared.screens.main.MainViewModel
import net.schacher.mcc.shared.screens.mydecks.MyDecksViewModel
import net.schacher.mcc.shared.screens.newdeck.NewDeckViewModel
import net.schacher.mcc.shared.screens.packselection.PackSelectionViewModel
import net.schacher.mcc.shared.screens.search.SearchViewModel
import net.schacher.mcc.shared.screens.settings.SettingsViewModel
import net.schacher.mcc.shared.screens.spotlight.SpotlightViewModel
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val network = module {
    singleOf<MarvelCDbDataSource>(::KtorMarvelCDbDataSource)
}

val repositories = module {
    singleOf(::CardRepository)
    singleOf(::DeckRepository)
    singleOf(::PackRepository)
}

val viewModels = module {
    singleOf(::MainViewModel)
    singleOf(::MyDecksViewModel)
    singleOf(::NewDeckViewModel)
    singleOf(::SettingsViewModel)
    singleOf(::SearchViewModel)
    singleOf(::SpotlightViewModel)
    singleOf(::PackSelectionViewModel)
}

@Composable
fun App(
    databaseDao: DatabaseDao,
    onKoinStart: KoinApplication.() -> Unit = {}
) {
    KoinApplication(
        application = {
            onKoinStart()
            modules(
                platformModule,
                module {
                    single<CardDatabaseDao> { databaseDao }
                    single<DeckDatabaseDao> { databaseDao }
                    single<PackDatabaseDao> { databaseDao }
                    single<SettingsDao> { databaseDao }
                },
                network,
                repositories,
                viewModels
            )
        }) {
        MccTheme {
            var loggedIn by remember { mutableStateOf(AuthHandler.loggedIn) }

            if (!loggedIn) {
                LoginScreen { loggedIn = true }
            } else {
                MainScreen()
            }
        }
    }
}
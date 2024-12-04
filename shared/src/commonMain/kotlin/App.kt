import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import net.schacher.mcc.shared.datasource.database.CardDatabaseDao
import net.schacher.mcc.shared.datasource.database.DatabaseDao
import net.schacher.mcc.shared.datasource.database.PackDatabaseDao
import net.schacher.mcc.shared.datasource.database.SettingsDao
import net.schacher.mcc.shared.datasource.http.DefaultClient
import net.schacher.mcc.shared.datasource.http.KtorMarvelCDbDataSource
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.design.theme.MccTheme
import net.schacher.mcc.shared.platform.platformModule
import net.schacher.mcc.shared.repositories.AuthRepository
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.repositories.PackRepository
import net.schacher.mcc.shared.repositories.SpotlightRepository
import net.schacher.mcc.shared.screens.app.AppScreen
import net.schacher.mcc.shared.screens.app.AppViewModel
import net.schacher.mcc.shared.screens.card.CardScreenViewModel
import net.schacher.mcc.shared.screens.collection.CollectionViewModel
import net.schacher.mcc.shared.screens.deck.DeckScreenViewModel
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
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import pro.schacher.mcc.BuildConfig

val network = module {
    single<MarvelCDbDataSource> {
        KtorMarvelCDbDataSource(
            DefaultClient,
            get(),
            BuildConfig.SERVICE_URL,
        )
    }
}

val repositories = module {
    singleOf(::CardRepository)
    singleOf(::DeckRepository)
    singleOf(::PackRepository)
    singleOf(::SpotlightRepository)
}

val viewModels = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::NewDeckViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::PackSelectionViewModel)
    viewModelOf(::MyDecksViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::SpotlightViewModel)
    viewModelOf(::CollectionViewModel)
    viewModel { (deckId: Int) -> DeckScreenViewModel(deckId, get(), get()) }
    viewModel { (cardCode: String) -> CardScreenViewModel(cardCode, get(), get(), get()) }
}

@Composable
fun App(
    databaseDao: DatabaseDao,
    onKoinStart: KoinApplication.() -> Unit = {},
    onQuitApp: () -> Unit = {},
    onLoginClicked: (LoginBridge) -> Unit,
) {
    val authHandler = AuthRepository(databaseDao as SettingsDao)
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    KoinApplication(application = {
        onKoinStart()
        modules(
            platformModule,
            module {
                single<CoroutineScope> { MainScope() }
            },
            module {
                single<CardDatabaseDao> { databaseDao }
                single<PackDatabaseDao> { databaseDao }
                single<SettingsDao> { databaseDao }
            },
            module {
                single<AuthRepository> { authHandler }
            },
            module {
                single<NavController> { navController }
            },
            module {
                single<SnackbarHostState> { snackbarHostState }
            },
            network,
            repositories,
            viewModels
        )
    }) {
        MccTheme {
            AppScreen(
                onLogInClicked = {
                    onLoginClicked.invoke(
                        object : LoginBridge {
                            override fun handleCallbackUrl(callbackUrl: String) {
                                authHandler.handleCallbackUrl(callbackUrl)
                            }
                        })
                },
                onQuitApp = onQuitApp
            )
        }
    }
}

/**
 * This interface is used to bridge the login process between the shared code and the iOS platform code.
 * It's needed to make use of the modal presentation of the Safari web view controller.
 */
interface LoginBridge {

    val url: String
        get() = BuildConfig.OAUTH_URL

    fun handleCallbackUrl(callbackUrl: String)

    fun isCallbackUrl(url: String): Boolean = url.startsWith("mccapp://callback")
}
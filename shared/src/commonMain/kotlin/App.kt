import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.auth.AuthHandler
import net.schacher.mcc.shared.auth.PersistingAuthHandler
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
import net.schacher.mcc.shared.utils.debug
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val network = module {
    single<MarvelCDbDataSource> { KtorMarvelCDbDataSource(get()) }
}

val repositories = module {
    singleOf(::CardRepository)
    singleOf(::DeckRepository)
    singleOf(::PackRepository)
}

val viewModels = module {
    singleOf(::AppViewModel)
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
    databaseDao: DatabaseDao, onKoinStart: KoinApplication.() -> Unit = {}
) {
    val authHandler = PersistingAuthHandler(databaseDao as SettingsDao)
    KoinApplication(application = {
        onKoinStart()
        modules(platformModule, module {
            single<CardDatabaseDao> { databaseDao }
            single<DeckDatabaseDao> { databaseDao }
            single<PackDatabaseDao> { databaseDao }
            single<SettingsDao> { databaseDao }
        }, module {
            single<AuthHandler> { authHandler }
        }, network, repositories, viewModels
        )
    }) {
        MccTheme {
            AppScreen()
        }
    }
}

@Composable
fun AppScreen(appViewModel: AppViewModel = koinInject()) {
    val loggedIn = appViewModel.state.collectAsState()
    if (loggedIn.value) {
        MainScreen()
    } else {
        LoginScreen {
            appViewModel.onGuestLoginClicked()
        }
    }
}

class AppViewModel(
    private val authHandler: AuthHandler
) : ViewModel() {

    private val _state = MutableStateFlow(authHandler.isLoggedIn())

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            authHandler.loginState.collect {
                Logger.debug { "Login state changed: $it" }
                _state.value = it
            }
        }
    }

    fun onGuestLoginClicked() {
        _state.value = true
    }
}
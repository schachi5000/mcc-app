import androidx.compose.runtime.Composable
import net.schacher.mcc.shared.datasource.database.CardDatabaseDao
import net.schacher.mcc.shared.datasource.database.DatabaseDao
import net.schacher.mcc.shared.datasource.database.DeckDatabaseDao
import net.schacher.mcc.shared.datasource.database.PackDatabaseDao
import net.schacher.mcc.shared.datasource.database.SettingsDao
import net.schacher.mcc.shared.datasource.http.KtorMarvelCDbDataSource
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.design.theme.MccTheme
import net.schacher.mcc.shared.platform.platformModule
import net.schacher.mcc.shared.repositories.AuthRepository
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.repositories.PackRepository
import net.schacher.mcc.shared.screens.app.AppScreen
import net.schacher.mcc.shared.screens.app.AppViewModel
import net.schacher.mcc.shared.screens.login.LoginScreenViewModel
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
import pro.schacher.mcc.BuildConfig

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
    singleOf(::LoginScreenViewModel)
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
    onLoginClicked: (LoginBridge) -> Unit = {},
    onKoinStart: KoinApplication.() -> Unit = {}
) {
    val authHandler = AuthRepository(databaseDao as SettingsDao)
    KoinApplication(application = {
        onKoinStart()
        modules(platformModule, module {
            single<CardDatabaseDao> { databaseDao }
            single<DeckDatabaseDao> { databaseDao }
            single<PackDatabaseDao> { databaseDao }
            single<SettingsDao> { databaseDao }
        }, module {
            single<AuthRepository> { authHandler }
        }, network, repositories, viewModels
        )
    }) {
        MccTheme {
            AppScreen(onLogInClicked = {
                onLoginClicked(object : LoginBridge {
                    override val url: String = BuildConfig.OAUTH_URL
                    override fun onLoginSuccessful(callbackUrl: String) {
                        authHandler.handleCallbackUrl(callbackUrl)
                    }
                })
            })
        }
    }
}

/**
 * This interface is used to bridge the login process between the shared code and the iOS platform code.
 * It's needed to make use of the modal presentation of the Safari web view controller.
 */
interface LoginBridge {

    val url: String

    fun onLoginSuccessful(callbackUrl: String)

    fun isCallbackUrl(url: String): Boolean = url.startsWith("mccapp://callback")
}






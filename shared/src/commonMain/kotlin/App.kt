import androidx.compose.runtime.Composable
import net.schacher.mcc.shared.database.CardDatabaseDao
import net.schacher.mcc.shared.database.DatabaseDao
import net.schacher.mcc.shared.database.DeckDatabaseDao
import net.schacher.mcc.shared.database.SettingsDao
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
            module { single<CardDatabaseDao> { databaseDao } },
            module { single<DeckDatabaseDao> { databaseDao } },
            module { single<SettingsDao> { databaseDao } },
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

enum class Platform {
    ANDROID, IOS;
}

expect val platform: Platform

val IS_IOS: Boolean
    get() = platform == Platform.IOS

val IS_ANDROID: Boolean
    get() = platform == Platform.ANDROID
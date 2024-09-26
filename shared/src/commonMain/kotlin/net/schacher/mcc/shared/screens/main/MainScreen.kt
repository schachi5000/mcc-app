package net.schacher.mcc.shared.screens.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.collection
import marvelchampionscompanion.shared.generated.resources.my_decks
import marvelchampionscompanion.shared.generated.resources.settings
import marvelchampionscompanion.shared.generated.resources.spotlight
import net.schacher.mcc.shared.design.compose.BackHandler
import net.schacher.mcc.shared.design.compose.PagerHeader
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.screens.AppScreen
import net.schacher.mcc.shared.screens.collection.CollectionScreen
import net.schacher.mcc.shared.screens.main.MainViewModel.Event.CardsDatabaseSyncFailed
import net.schacher.mcc.shared.screens.main.MainViewModel.Event.DatabaseSynced
import net.schacher.mcc.shared.screens.main.MainViewModel.Event.DeckCreated
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.Collection
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.MyDecks
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.Settings
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.Spotlight
import net.schacher.mcc.shared.screens.mydecks.MyDecksScreen
import net.schacher.mcc.shared.screens.settings.SettingsScreen
import net.schacher.mcc.shared.screens.spotlight.SpotlightScreen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

internal val topInset = 2 * ContentPadding + 64.dp

@OptIn(
    ExperimentalResourceApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun MainScreen(
    viewModel: MainViewModel = koinInject(),
    navController: NavController = koinInject(),
) {
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler {
        viewModel.onLogoutClicked()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MaterialTheme.colors.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            val pageLabels = listOf(
                Spotlight, MyDecks,
                Collection, Settings
            )
            val pagerState = rememberPagerState(pageCount = { pageLabels.size })

            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    Spotlight.tabIndex -> SpotlightScreen(topInset = topInset) {
                        navController.navigate("deck/${it.id}")
                    }

                    MyDecks.tabIndex -> MyDecksScreen(topInset = topInset,
                        onDeckClick = {
                            navController.navigate("deck/${it.id}")
                        },
                        onAddDeckClick = {
                            navController.navigate(AppScreen.AddDeck.route)
                        })

                    Collection.tabIndex -> CollectionScreen(topInset = topInset) {
                        navController.navigate("card/${it.code}")
                    }

                    Settings.tabIndex -> SettingsScreen(
                        topInset = topInset,
                        onPackSelectionClick = {
                            navController.navigate(AppScreen.Packs.route)
                        },
                        onLogoutClicked = {
                            viewModel.onLogoutClicked()
                        })
                }
            }

            PagerHeader(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colors.background.copy(alpha = .9f))
                    .statusBarsPadding()
                    .padding(vertical = ContentPadding * 2),
                pageLabels = pageLabels.map { it.label },
                pagerState = pagerState,
            ) {
                scope.launch {
                    pagerState.animateScrollToPage(it)
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                DatabaseSynced -> snackbarHostState.showSnackbar("Database synced!")
                is CardsDatabaseSyncFailed -> snackbarHostState.showSnackbar("Error fully syncing database")
                is DeckCreated -> snackbarHostState.showSnackbar("Deck created! ${it.deckName}")
            }
        }
    }
}

private val MainScreen.tabIndex: Int
    get() = when (this) {
        Spotlight -> 0
        MyDecks -> 1
        Collection -> 2
        Settings -> 3
    }

private val MainScreen.label: String
    @Composable
    get() = when (this) {
        Spotlight -> stringResource(Res.string.spotlight)
        MyDecks -> stringResource(Res.string.my_decks)
        Collection -> stringResource(Res.string.collection)
        Settings -> stringResource(Res.string.settings)
    }
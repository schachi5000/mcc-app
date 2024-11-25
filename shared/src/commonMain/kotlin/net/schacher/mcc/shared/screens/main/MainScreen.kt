package net.schacher.mcc.shared.screens.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.collection
import marvelchampionscompanion.shared.generated.resources.ic_collection
import marvelchampionscompanion.shared.generated.resources.ic_collection_selected
import marvelchampionscompanion.shared.generated.resources.ic_my_decks
import marvelchampionscompanion.shared.generated.resources.ic_my_decks_selected
import marvelchampionscompanion.shared.generated.resources.ic_spotlight
import marvelchampionscompanion.shared.generated.resources.ic_spotlight_selected
import marvelchampionscompanion.shared.generated.resources.my_decks
import marvelchampionscompanion.shared.generated.resources.settings
import marvelchampionscompanion.shared.generated.resources.spotlight
import net.schacher.mcc.shared.screens.AppRoute
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
import net.schacher.mcc.shared.screens.navigate
import net.schacher.mcc.shared.screens.settings.SettingsScreen
import net.schacher.mcc.shared.screens.spotlight.SpotlightScreen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

internal val topInset = 64.dp

@OptIn(
    ExperimentalResourceApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
    navController: NavController = koinInject(),
    snackbarHostState: SnackbarHostState = koinInject(),
) {
    val state = viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MaterialTheme.colors.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomBar(state.value.mainScreen.tabIndex) {
                viewModel.onTabSelected(it.toMainScreen())
            }
        }
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            val pageLabels = listOf(
                Spotlight,
                MyDecks,
                Collection,
                Settings
            )

            val pagerState = rememberPagerState(
                pageCount = { pageLabels.size },
                initialPage = state.value.mainScreen.tabIndex
            )

            LaunchedEffect(state.value.mainScreen.tabIndex) {
                pagerState.animateScrollToPage(state.value.mainScreen.tabIndex)
            }

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false
            ) { page ->
                when (page) {
                    Spotlight.tabIndex -> SpotlightScreen {
                        navController.navigate("deck/${it.id}")
                    }

                    MyDecks.tabIndex -> MyDecksScreen(
                        onDeckClick = {
                            navController.navigate("deck/${it.id}")
                        },
                        onAddDeckClick = {
                            navController.navigate(AppRoute.AddDeck)
                        })

                    Collection.tabIndex -> CollectionScreen {
                        navController.navigate("card/${it.code}")
                    }

                    Settings.tabIndex -> SettingsScreen(
                        onPackSelectionClick = {
                            navController.navigate(AppRoute.Packs)
                        },
                        onLogoutClicked = {
                            viewModel.onLogoutClicked()
                        })
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

@Composable
fun BottomBar(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    BottomNavigation(
        modifier = Modifier.fillMaxWidth(),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.background,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(72.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DefaultBottomNavigationItem(
                drawableResource = Res.drawable.ic_spotlight,
                selectedDrawableResource = Res.drawable.ic_spotlight_selected,
                selected = (selectedTabIndex == Spotlight.tabIndex),
                onClick = { onTabSelected(Spotlight.tabIndex) },
            )
            DefaultBottomNavigationItem(
                drawableResource = Res.drawable.ic_my_decks,
                selectedDrawableResource = Res.drawable.ic_my_decks_selected,
                selected = (selectedTabIndex == MyDecks.tabIndex),
                onClick = { onTabSelected(MyDecks.tabIndex) },
            )
            DefaultBottomNavigationItem(
                drawableResource = Res.drawable.ic_collection,
                selectedDrawableResource = Res.drawable.ic_collection_selected,
                selected = (selectedTabIndex == Collection.tabIndex),
                onClick = { onTabSelected(Collection.tabIndex) },
            )
            DefaultBottomNavigationItem(
                imageVector = Icons.Rounded.MoreVert,
                selected = (selectedTabIndex == Settings.tabIndex),
                onClick = { onTabSelected(Settings.tabIndex) },
            )
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

private fun Int.toMainScreen(): MainScreen = when (this) {
    1 -> MyDecks
    2 -> Collection
    3 -> Settings
    else -> Spotlight
}

private val MainScreen.label: String
    @Composable
    get() = when (this) {
        Spotlight -> stringResource(Res.string.spotlight)
        MyDecks -> stringResource(Res.string.my_decks)
        Collection -> stringResource(Res.string.collection)
        Settings -> stringResource(Res.string.settings)
    }
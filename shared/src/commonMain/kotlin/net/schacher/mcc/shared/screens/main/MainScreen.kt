package net.schacher.mcc.shared.screens.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
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
import net.schacher.mcc.shared.design.compose.BackHandler
import net.schacher.mcc.shared.design.compose.BottomSheetContainer
import net.schacher.mcc.shared.design.theme.BottomSheetColors
import net.schacher.mcc.shared.design.theme.BottomSheetShape
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.platform.isIOs
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
import net.schacher.mcc.shared.screens.settings.MoreScreen
import net.schacher.mcc.shared.screens.spotlight.SpotlightScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
    navController: NavController = koinInject(),
    snackbarHostState: SnackbarHostState = koinInject(),
) {
    val state = viewModel.state.collectAsState()

    val scope = rememberCoroutineScope()
    var currentContent by remember { mutableStateOf<@Composable () -> Unit>({ }) }

    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val bottomSheetDelegate by remember {
        mutableStateOf(BottomSheetDelegate(
            onShow = {
                currentContent = it
                scope.launch { bottomSheetState.show() };
            },
            onHide = {
                scope.launch {
                    bottomSheetState.hide()
                }
            }
        ))
    }

    BackHandler(bottomSheetState.isVisible) {
        scope.launch {
            bottomSheetState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = BottomSheetShape,
        sheetBackgroundColor = BottomSheetColors.Background,
        scrimColor = BottomSheetColors.Scrim,
        sheetContent = {
            BottomSheetContainer {
                currentContent()
            }
        }
    ) {
        Content(
            snackbarHostState,
            state,
            viewModel,
            navController,
            bottomSheetDelegate
        )
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
private fun Content(
    snackbarHostState: SnackbarHostState,
    state: State<MainViewModel.UiState>,
    viewModel: MainViewModel,
    navController: NavController,
    bottomSheetDelegate: BottomSheetDelegate,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MaterialTheme.colors.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomBar(state.value.mainScreen.tabIndex) {
                viewModel.onTabSelected(it.toMainScreen())
            }
        },
    ) {
        Box(
            modifier = Modifier.padding(it),
        ) {
            var previousState by remember { mutableStateOf(state.value.mainScreen) }

            AnimatedContent(
                targetState = state.value.mainScreen,
                transitionSpec = pagerTransitionSpec(previousState)
            ) {
                when (it) {
                    Spotlight -> SpotlightScreen {
                        navController.navigate("deck/${it.id}")
                    }

                    MyDecks -> MyDecksScreen(
                        onDeckClick = {
                            navController.navigate("deck/${it.id}")
                        },
                        onAddDeckClick = {
                            navController.navigate(AppRoute.AddDeck)
                        },
                        onLoginClick = {
                            viewModel.onLogoutClicked()
                        }
                    )

                    Collection -> CollectionScreen(
                        bottomSheetDelegate = bottomSheetDelegate,
                    )

                    Settings -> MoreScreen(
                        onPackSelectionClick = {
                            navController.navigate(AppRoute.Packs)
                        },
                        onLogoutClicked = {
                            viewModel.onLogoutClicked()
                        })
                }

                previousState = it
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
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = ContentPadding.takeIf { isIOs() } ?: 0.dp)
                .heightIn(72.dp),
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

private fun pagerTransitionSpec(lastState: MainScreen):
        AnimatedContentTransitionScope<MainScreen>.() -> ContentTransform = {
    if (targetState.tabIndex > lastState.tabIndex) {
        slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
    } else {
        slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
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

class BottomSheetDelegate(
    val onShow: (@Composable () -> Unit) -> Unit = {},
    val onHide: () -> Unit = {},
) {
    fun show(content: @Composable () -> Unit) {
        this.onShow(content)
    }

    fun hide() {
        this.onHide()
    }
}
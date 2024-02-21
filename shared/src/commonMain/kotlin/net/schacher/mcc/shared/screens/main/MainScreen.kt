package net.schacher.mcc.shared.screens.main

import IS_IOS
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.decks
import marvelchampionscompanion.shared.generated.resources.ic_deck
import marvelchampionscompanion.shared.generated.resources.ic_search
import marvelchampionscompanion.shared.generated.resources.ic_spotlight
import marvelchampionscompanion.shared.generated.resources.more
import marvelchampionscompanion.shared.generated.resources.search
import marvelchampionscompanion.shared.generated.resources.spotlight
import net.schacher.mcc.shared.design.compose.BackHandler
import net.schacher.mcc.shared.design.compose.CardInfo
import net.schacher.mcc.shared.design.compose.FreeBottomSheetContainer
import net.schacher.mcc.shared.design.compose.blurByBottomSheet
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.screens.deck.DeckScreen
import net.schacher.mcc.shared.screens.main.MainViewModel.Event.CardsDatabaseSyncFailed
import net.schacher.mcc.shared.screens.main.MainViewModel.Event.DatabaseSynced
import net.schacher.mcc.shared.screens.main.MainViewModel.Event.DeckCreated
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.FullScreen.CreateDeck
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.FullScreen.DeckScreen
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.FullScreen.PackSelectionScreen
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.Decks
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.Search
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.Settings
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.Spotlight
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.SubScreen.CardMenu
import net.schacher.mcc.shared.screens.mydecks.MyDecksScreen
import net.schacher.mcc.shared.screens.newdeck.NewDeckScreen
import net.schacher.mcc.shared.screens.packselection.PackSelectionScreen
import net.schacher.mcc.shared.screens.search.SearchScreen
import net.schacher.mcc.shared.screens.settings.SettingsScreen
import net.schacher.mcc.shared.screens.splash.SplashScreen
import net.schacher.mcc.shared.screens.spotlight.SpotlightScreen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterialApi::class, ExperimentalResourceApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel = koinInject()) {
    val state = viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler(
        enabled = (state.value.subScreen != null ||
                state.value.fullScreen != null)
    ) {
        viewModel.onBackPressed()
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        scrimColor = Color.Black.copy(alpha = 0.35f),
        sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
        sheetBackgroundColor = MaterialTheme.colors.surface,
        sheetContent = {
            state.value.subScreen?.let {
                when (it) {
                    is CardMenu -> CardMenuBottomSheet(viewModel, it.card)
                    else -> {}
                }
            }
        }) {

        Scaffold(
            modifier = Modifier.fillMaxSize().blurByBottomSheet(sheetState),
            backgroundColor = MaterialTheme.colors.background,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                BottomBar(state.value.mainScreen.tabIndex) {
                    viewModel.onTabSelected(it)
                }
            }
        ) {
            Box(
                modifier = Modifier.padding(it)
            ) {
                AnimatedContent(
                    targetState = state.value.mainScreen.tabIndex,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut())
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut())
                        }
                    }) { state ->
                    when (state) {
                        0 -> MyDecksScreen(
                            onDeckClick = { viewModel.onDeckClicked(it) },
                            onAddDeckClick = { viewModel.onNewDeckClicked() }
                        )

                        1 -> SpotlightScreen {
                            viewModel.onDeckClicked(it)
                        }

                        2 -> SearchScreen {
                            viewModel.onCardClicked(it)
                        }

                        3 -> SettingsScreen {
                            viewModel.onPackSelectionClicked()
                        }
                    }
                }
            }
        }
    }

    scope.launch {
        if (state.value.subScreen != null) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                DatabaseSynced -> snackbarHostState.showSnackbar("Database synced!")
                is CardsDatabaseSyncFailed -> snackbarHostState.showSnackbar("Error syncing database: ${it.exception.message}")
                is DeckCreated -> snackbarHostState.showSnackbar("Deck created! ${it.deckName}")
            }
        }
    }

    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.isVisible }.collect { isVisible ->
            if (!isVisible) {
                viewModel.onContextMenuClosed()
            }
        }
    }

    AnimatedContent(
        targetState = state.value.fullScreen,
        transitionSpec = {
            (slideInVertically { height -> height } + fadeIn()).togetherWith(
                slideOutVertically { height -> height } + fadeOut())
        }
    ) {
        when (it) {
            is DeckScreen -> DeckScreen(
                it.deck,
                onCloseClick = { viewModel.onBackPressed() },
                onDeleteDeckClick = { viewModel.onRemoveDeckClick(it) }
            )

            is PackSelectionScreen -> PackSelectionScreen {
                viewModel.onBackPressed()
            }

            is CreateDeck -> NewDeckScreen(
                onBackPress = { viewModel.onBackPressed() },
                onNewDeckSelected = { card, aspect ->
                    viewModel.onNewDeckHeroSelected(card, aspect)
                }
            )

            else -> {
                Box(modifier = Modifier.fillMaxSize().background(Color.Transparent))
            }
        }
    }

    AnimatedVisibility(
        visible = state.value.splash != null,
        exit = fadeOut() + slideOutVertically { fullHeight -> fullHeight },
    ) {
        SplashScreen((state.value.splash)?.preparing ?: false)
    }


}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BottomBar(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    BottomNavigation(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(72.dp)
            .graphicsLayer {
                clip = true
                shape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp)
            },
        backgroundColor = MaterialTheme.colors.surface,
    ) {
        Row(Modifier.fillMaxWidth().padding(bottom = if (IS_IOS) 16.dp else 0.dp)) {
            DefaultBottomNavigationItem(
                label = stringResource(Res.string.decks),
                icon = Res.drawable.ic_deck,
                color = Decks.tabColor,
                selected = (selectedTabIndex == 0),
                onClick = { onTabSelected(0) },
            )
            DefaultBottomNavigationItem(
                label = stringResource(Res.string.spotlight),
                icon = Res.drawable.ic_spotlight,
                color = Spotlight.tabColor,
                selected = (selectedTabIndex == 1),
                onClick = { onTabSelected(1) },
            )
            DefaultBottomNavigationItem(
                label = stringResource(Res.string.search),
                icon = Res.drawable.ic_search,
                color = Search.tabColor,
                selected = (selectedTabIndex == 2),
                onClick = { onTabSelected(2) },
            )
            DefaultBottomNavigationItem(
                label = stringResource(Res.string.more),
                icon = { Icon(Icons.Rounded.Settings, "Settings") },
                color = Settings.tabColor,
                selected = (selectedTabIndex == 3),
                onClick = { onTabSelected(3) },
            )
        }
    }
}

@Composable
fun CardMenuBottomSheet(mainViewModel: MainViewModel, card: Card) {
    FreeBottomSheetContainer(modifier = Modifier.fillMaxHeight(0.75f)) {
        CardInfo(card = card)
    }
}

private val MainScreen.tabColor: Color
    get() = when (this) {
        Decks -> Color(0xfff78f3f)
        Spotlight -> Color(0xff31e29c)
        Search -> Color(0xff518cca)
        Settings -> Color(0xff9957ff)
    }

private val MainScreen.tabIndex: Int
    get() = when (this) {
        Decks -> 0
        Spotlight -> 1
        Search -> 2
        Settings -> 3
    }
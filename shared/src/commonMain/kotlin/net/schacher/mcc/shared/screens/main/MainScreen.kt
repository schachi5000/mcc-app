package net.schacher.mcc.shared.screens.main

import IS_IOS
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
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
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.design.compose.BottomSheetContainer
import net.schacher.mcc.shared.design.compose.CardInfo
import net.schacher.mcc.shared.design.compose.FreeBottomSheetContainer
import net.schacher.mcc.shared.design.compose.InspectScreen
import net.schacher.mcc.shared.design.compose.OptionsEntry
import net.schacher.mcc.shared.localization.Localization
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.screens.deck.DeckScreen
import net.schacher.mcc.shared.screens.featured.FeaturedScreen
import net.schacher.mcc.shared.screens.main.MainUiState.MainScreen.Decks
import net.schacher.mcc.shared.screens.main.MainUiState.MainScreen.Featured
import net.schacher.mcc.shared.screens.main.MainUiState.MainScreen.Search
import net.schacher.mcc.shared.screens.main.MainUiState.MainScreen.Settings
import net.schacher.mcc.shared.screens.main.MainUiState.SubScreen.CardMenu
import net.schacher.mcc.shared.screens.main.MainUiState.SubScreen.DeckInspector
import net.schacher.mcc.shared.screens.main.MainUiState.SubScreen.DeckMenu
import net.schacher.mcc.shared.screens.search.SearchScreen
import net.schacher.mcc.shared.screens.settings.SettingsScreen
import net.schacher.mcc.shared.screens.splash.SplashScreen
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = koinInject()
) {
    val state = mainViewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val snackbarHostState = remember { SnackbarHostState() }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        scrimColor = Color.Black.copy(alpha = 0.25f),
        sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
        sheetBackgroundColor = MaterialTheme.colors.surface,
        sheetContent = {
            state.value.subScreen?.let {
                when (it) {
                    is CardMenu -> CardMenuBottomSheet(mainViewModel, it.card)
                    is DeckMenu -> DeckMenuBottomSheet(mainViewModel, it.deck)
                    is DeckInspector -> DeckInspectorBottomSheet(mainViewModel, it.deck)
                    else -> {}
                }
            }
        }) {

        Scaffold(
            Modifier.fillMaxSize().blur(if (state.value.subScreen != null) 4.dp else 0.dp),
            backgroundColor = MaterialTheme.colors.background,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                BottomBar(state.value.mainScreen.tabIndex) {
                    mainViewModel.onTabSelected(it)
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
                            slideInHorizontally { width -> width } + fadeIn() with
                                    slideOutHorizontally { width -> -width } + fadeOut()
                        } else {
                            slideInHorizontally { width -> -width } + fadeIn() with
                                    slideOutHorizontally { width -> width } + fadeOut()
                        }
                    }) { state ->
                    when (state) {
                        0 -> DeckScreen(
                            onDeckClick = { mainViewModel.onDeckClicked(it) },
                            onAddDeckClick = {}
                        )

                        1 -> FeaturedScreen {
                            mainViewModel.onDeckClicked(it)
                        }

                        2 -> SearchScreen {
                            mainViewModel.onCardClicked(it)
                            scope.launch {
                                snackbarHostState.showSnackbar("${it.name} | ${it.code}")
                            }
                        }

                        3 -> SettingsScreen()
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

    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.isVisible }.collect { isVisible ->
            if (!isVisible) {
                mainViewModel.onContextMenuClosed()
            }
        }
    }

    AnimatedVisibility(
        visible = state.value.splash != null,
        exit = fadeOut()
    ) {
        SplashScreen((state.value.splash)?.preparing ?: false)
    }
}


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
                label = Localization.MY_DECKS,
                icon = "ic_deck.xml",
                color = Decks.tabColor,
                selected = (selectedTabIndex == 0),
                onClick = { onTabSelected(0) },
            )
            DefaultBottomNavigationItem(
                label = Localization.SPOTLIGHT,
                icon = "ic_featured_decks.xml",
                color = Featured.tabColor,
                selected = (selectedTabIndex == 1),
                onClick = { onTabSelected(1) },
            )
            DefaultBottomNavigationItem(
                label = Localization.SEARCH,
                icon = "ic_search.xml",
                color = Search.tabColor,
                selected = (selectedTabIndex == 2),
                onClick = { onTabSelected(2) },
            )
            DefaultBottomNavigationItem(
                label = Localization.MORE,
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

@Composable
fun DeckMenuBottomSheet(mainViewModel: MainViewModel, deck: Deck) {
    BottomSheetContainer {
        OptionsEntry(
            label = "Löschen",
            imageVector = Icons.Rounded.Delete
        ) {
            mainViewModel.onRemoveDeckClick(deck)
        }
    }
}

@Composable
fun DeckInspectorBottomSheet(mainViewModel: MainViewModel, deck: Deck) {
    BottomSheetContainer(modifier = Modifier.fillMaxHeight(0.9f)) {
        InspectScreen(cards = deck.cards) {}
    }
}

private val MainUiState.MainScreen.tabColor: Color
    get() = when (this) {
        Decks -> Color(0xfff78f3f)
        Featured -> Color(0xff31e29c)
        Search -> Color(0xff518cca)
        Settings -> Color(0xff9957ff)
    }

private val MainUiState.MainScreen.tabIndex: Int
    get() = when (this) {
        Decks -> 0
        Featured -> 1
        Search -> 2
        Settings -> 3
    }
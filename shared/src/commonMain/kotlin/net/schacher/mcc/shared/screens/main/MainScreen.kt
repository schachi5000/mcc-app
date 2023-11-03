package net.schacher.mcc.shared.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.design.compose.DefaultBottomNavigationItem
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.screens.deck.DeckScreen
import net.schacher.mcc.shared.screens.deck.DeckViewModel
import net.schacher.mcc.shared.screens.featured.FeaturedScreen
import net.schacher.mcc.shared.screens.featured.FeaturedViewModel
import net.schacher.mcc.shared.screens.search.SearchScreen
import net.schacher.mcc.shared.screens.search.SearchViewModel
import net.schacher.mcc.shared.screens.settings.SettingsScreen
import net.schacher.mcc.shared.screens.settings.SettingsViewModel
import net.schacher.mcc.shared.screens.splash.SplashScreen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel, cardRepository: CardRepository, deckRepository: DeckRepository) {
    val state = mainViewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val snackbarHostState = remember { SnackbarHostState() }


    val featuredViewModel = getViewModel(Unit, viewModelFactory { FeaturedViewModel(cardRepository) })
    val deckViewModel = getViewModel(Unit, viewModelFactory { DeckViewModel(deckRepository, cardRepository) })
    val searchViewModel = getViewModel(Unit, viewModelFactory { SearchViewModel(cardRepository) })
    val settingsViewModel =
        getViewModel(Unit, viewModelFactory { SettingsViewModel(cardRepository, deckRepository) })

    ModalBottomSheetLayout(
        sheetState = sheetState,
        scrimColor = MaterialTheme.colors.background.copy(alpha = 0.2f),
        sheetContent = {
            // TODO Add context sheets here
        }) {
        Scaffold(
            Modifier.fillMaxSize(),
            backgroundColor = MaterialTheme.colors.background,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                BottomBar(state.value.selectedTabIndex) {
                    mainViewModel.onTabSelected(it)
                }
            }
        ) {
            Box(modifier = Modifier.padding(it)) {
                Logger.d { "selectedTabIndex: ${state.value.selectedTabIndex}" }
                when (state.value.selectedTabIndex) {
                    0 -> DeckScreen(deckViewModel)
                    1 -> FeaturedScreen(featuredViewModel)
                    2 -> SearchScreen(searchViewModel) {
                        scope.launch { snackbarHostState.showSnackbar("${it.name} | ${it.code}") }
                    }

                    3 -> SettingsScreen(settingsViewModel)
                }
            }
        }
    }

    AnimatedVisibility(
        visible = state.value.preparingApp,
        exit = fadeOut()
    ) {
        SplashScreen()
    }
}


@Composable
fun BottomBar(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    BottomNavigation(
        modifier = Modifier.fillMaxWidth()
            .height(72.dp)
            .graphicsLayer {
                clip = true
                shape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp)
            },
        backgroundColor = MaterialTheme.colors.surface,
    ) {
        DefaultBottomNavigationItem(
            label = "Decks",
            icon = "ic_deck.xml",
            color = Color(0xfff78f3f),
            selected = (selectedTabIndex == 0),
            onClick = { onTabSelected(0) },
        )
        DefaultBottomNavigationItem(
            label = "Featured",
            icon = "ic_featured_decks.xml",
            color = Color(0xff31e29c),
            selected = (selectedTabIndex == 1),
            onClick = { onTabSelected(1) },
        )
        DefaultBottomNavigationItem(
            label = "Suche",
            icon = "ic_search.xml",
            color = Color(0xff518cca),
            selected = (selectedTabIndex == 2),
            onClick = { onTabSelected(2) },
        )
        DefaultBottomNavigationItem(
            label = "Einstellungen",
            icon = { Icon(Icons.Rounded.Settings, "Settings") },
            color = Color(0xffe74c3c),
            selected = (selectedTabIndex == 3),
            onClick = { onTabSelected(3) },
        )
    }
}
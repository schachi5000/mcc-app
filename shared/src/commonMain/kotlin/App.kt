import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.database.DatabaseDao
import net.schacher.mcc.shared.design.DefaultBottomNavigationItem
import net.schacher.mcc.shared.design.OptionsEntry
import net.schacher.mcc.shared.design.theme.DarkColorScheme
import net.schacher.mcc.shared.design.theme.LightColorScheme
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.screens.deck.DeckScreen
import net.schacher.mcc.shared.screens.deck.DeckViewModel
import net.schacher.mcc.shared.screens.featured.FeaturedScreen
import net.schacher.mcc.shared.screens.featured.FeaturedViewModel
import net.schacher.mcc.shared.screens.search.SearchScreen
import net.schacher.mcc.shared.screens.search.SearchViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun App(databaseDao: DatabaseDao) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    ) {
        val cardRepository = CardRepository(databaseDao)
        val deckRepository = DeckRepository(cardRepository, databaseDao)

        val scope = rememberCoroutineScope()

        val sheetState = rememberModalBottomSheetState(Hidden)
        val snackbarHostState = remember { SnackbarHostState() }

        val selectedTabIndex = remember { mutableStateOf(1) }

        ModalBottomSheetLayout(
            sheetState = sheetState,
            scrimColor = MaterialTheme.colors.background.copy(alpha = 0.2f),
            sheetContent = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Spacer(Modifier.height(8.dp))
                    OptionsEntry(label = "Datebank aktualisieren",
                        imageVector = Icons.Rounded.Refresh,
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                                snackbarHostState.showSnackbar("Datebank aktualisieren")
                                cardRepository.refresh()
                            }
                        })
                    OptionsEntry(
                        label = "Datebank löschen",
                        imageVector = Icons.Rounded.Delete,
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                                snackbarHostState.showSnackbar("Datebank wird gelöscht")
                                cardRepository.deleteAllCards()
                            }
                        })

                    Spacer(Modifier.height(32.dp))
                }
            }) {
            Scaffold(Modifier.fillMaxSize(),
                backgroundColor = MaterialTheme.colors.background,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = { BottomBar(selectedTabIndex) }
            ) {
                Box(modifier = Modifier.padding(it)) {
                    AnimatedContent(selectedTabIndex) {
                        Logger.d { "selectedTabIndex: ${it.value}" }
                        when (it.value) {
                            0 -> DeckScreen(getViewModel(Unit, viewModelFactory {
                                DeckViewModel(deckRepository, cardRepository)
                            }))

                            1 -> FeaturedScreen(getViewModel(Unit, viewModelFactory {
                                FeaturedViewModel()
                            }))

                            2 -> SearchScreen(getViewModel(Unit, viewModelFactory {
                                SearchViewModel(cardRepository)
                            })) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("${it.name} | ${it.code}")
                                }
                            }
                        }
                    }

                    Button(shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).size(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.surface
                        ),
                        onClick = {
                            scope.launch {
                                if (sheetState.isVisible) sheetState.hide() else sheetState.show()
                            }
                        }) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            "More",
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun BottomBar(onItemSelected: MutableState<Int>) {
    BottomNavigation(
        modifier = Modifier.fillMaxWidth().height(72.dp).graphicsLayer {
            clip = true
            shape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp)
        },
        backgroundColor = MaterialTheme.colors.surface,
    ) {
        DefaultBottomNavigationItem(
            label = "Decks",
            icon = "ic_collection.xml",
            color = Color(0xfff78f3f),
            selected = (onItemSelected.value == 0),
            onClick = { onItemSelected.value = 0 },
        )
        DefaultBottomNavigationItem(
            label = "Featured",
            icon = "ic_featured_decks.xml",
            color = Color(0xff31e29c),
            selected = (onItemSelected.value == 1),
            onClick = { onItemSelected.value = 1 },
        )
        DefaultBottomNavigationItem(
            label = "Suche",
            icon = "ic_search.xml",
            color = Color(0xff518cca),
            selected = (onItemSelected.value == 2),
            onClick = { onItemSelected.value = 2 },
        )
    }
}

expect fun getPlatformName(): String
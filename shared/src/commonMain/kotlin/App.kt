import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
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
import net.schacher.mcc.shared.search.SearchScreen
import net.schacher.mcc.shared.search.SearchViewModel

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

        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                Column(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Spacer(Modifier.height(8.dp))
                    OptionsEntry(
                        label = "Datebank aktualisieren",
                        imageVector = Icons.Rounded.Refresh,
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                                snackbarHostState.showSnackbar("Datebank aktualisieren")
                                cardRepository.refresh()
                            }
                        }
                    )
                    OptionsEntry(
                        label = "Datebank löschen",
                        imageVector = Icons.Rounded.Delete,
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                                snackbarHostState.showSnackbar("Datebank wird gelöscht")
                                cardRepository.deleteAllCards()
                            }
                        }
                    )

                    Spacer(Modifier.height(32.dp))
                }
            }) {
            Scaffold(
                Modifier.fillMaxSize(),
                backgroundColor = MaterialTheme.colors.background,
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                bottomBar = {
                    BottomBar { index, label ->
                        scope.launch {
                            when (index) {
                                3 -> if (!sheetState.isVisible) sheetState.show() else sheetState.hide()
                                else -> snackbarHostState.showSnackbar(label)
                            }
                        }
                    }
                }
            ) {
                Box(modifier = Modifier.padding(it)) {
                    val viewModel =
                        getViewModel(Unit, viewModelFactory { SearchViewModel(cardRepository) })
                    SearchScreen(viewModel, snackbarHostState) {
                        scope.launch {
                            snackbarHostState.showSnackbar("${it.name} | ${it.code}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar(onItemSelected: (Int, String) -> Unit) {
    val selectedIndex = remember { mutableStateOf(0) }

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
            icon = "ic_collection.xml",
            color = Color(0xfff78f3f),
            selected = (selectedIndex.value == 0),
            onClick = {
                selectedIndex.value = 0
                onItemSelected(0, "Decks")
            },
        )
        DefaultBottomNavigationItem(
            label = "Featured",
            icon = "ic_featured_decks.xml",
            color = Color(0xffe23636),
            selected = (selectedIndex.value == 1),
            onClick = {
                selectedIndex.value = 1
                onItemSelected(1, "Featured")
            },
        )
        DefaultBottomNavigationItem(
            label = "Suche",
            icon = "ic_search.xml",
            color = Color(0xff518cca),
            selected = (selectedIndex.value == 2),
            onClick = {
                selectedIndex.value = 2
                onItemSelected(2, "Suche")
            },
        )

        DefaultBottomNavigationItem(
            label = "Mehr",
            icon = { Icon(imageVector = Icons.Rounded.MoreVert, "More") },
            color = Color(0xff31e29c),
            selected = (selectedIndex.value == 3),
            onClick = {
                selectedIndex.value = 3
                onItemSelected(3, "Mehr")
            },
        )
    }
}


expect fun getPlatformName(): String
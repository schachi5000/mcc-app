import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import database.DatabaseDao
import design.theme.DarkColorScheme
import design.theme.LightColorScheme
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import repositories.CardRepository
import repositories.DeckRepository
import search.SearchScreen
import search.SearchViewModel

@Composable
fun App(databaseDao: DatabaseDao) {

    MaterialTheme(
        colors = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    ) {
        val cardRepository = CardRepository(databaseDao)
        val deckRepository = DeckRepository(cardRepository, databaseDao)

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        Scaffold(
            Modifier.fillMaxSize(),
            backgroundColor = MaterialTheme.colors.background,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            bottomBar = {
                BottomBar {
                    scope.launch {
                        when (it) {
                            1 -> {
                                snackbarHostState.showSnackbar("Refreshing")
                                cardRepository.refresh()
                            }

                            0 -> {
                                snackbarHostState.showSnackbar("Adding dummy deck")
                                deckRepository.addDummyDeck()
                            }
                        }
                    }
                }
            }
        ) {
            Box(modifier = Modifier.padding(it)) {
                val viewModel =
                    getViewModel(Unit, viewModelFactory { SearchViewModel(cardRepository) })
                SearchScreen(viewModel) {
                    scope.launch {
                        snackbarHostState.showSnackbar("${it.name} | ${it.code}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BottomBar(onItemSelected: (Int) -> Unit) {
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
        BottomNavigationItem(
            icon = {
                Icon(
                    painter = painterResource("ic_collection.xml"),
                    contentDescription = "Collection"
                )
            },
            label = { Text(text = "Decks") },
            selected = (selectedIndex.value == 0),
            onClick = {
                selectedIndex.value = 0
                onItemSelected(0)
            },
            selectedContentColor = Color(0xfff78f3f),
            unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.75f)
        )
        BottomNavigationItem(
            icon = {
                Icon(painterResource("ic_featured_decks.xml"), "Featured")
            },
            label = { Text(text = "Featured") },
            selected = (selectedIndex.value == 1),
            onClick = {
                selectedIndex.value = 1
                onItemSelected(1)
            },
            selectedContentColor = Color(0xffe23636),
            unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.75f)
        )
        BottomNavigationItem(
            icon = {
                Icon(painterResource("ic_search.xml"), "Search")
            },
            label = { Text(text = "Suche") },
            selected = (selectedIndex.value == 2),
            onClick = {
                selectedIndex.value = 2
                onItemSelected(2)
            },
            selectedContentColor = Color(0xff518cca),
            unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.75f)
        )
    }
}

expect fun getPlatformName(): String
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import design.compose.Entry
import design.compose.InspectScreen
import kotlinx.coroutines.launch
import provider.CardDataSource

@Composable
fun App() {
    MaterialTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        Scaffold(
            Modifier.windowInsetsPadding(WindowInsets.systemBars).fillMaxSize(),
            backgroundColor = Color(0xfff0f0f0),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            bottomBar = { BottomBar() }
        ) {
            Box(modifier = Modifier.padding(it)) {
                var cards by remember { mutableStateOf<List<Entry>>(emptyList()) }
                LaunchedEffect(Unit) {
                    cards = CardDataSource.getCardPack("core")
                        .groupBy { it.type }
                        .map { Entry("${it.key} (${it.value.size})", it.value) }
                }

                InspectScreen(entries = cards) {
                    scope.launch {
                        snackbarHostState.showSnackbar(it.name)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar() {
    val selectedIndex = remember { mutableStateOf(0) }
    BottomNavigation(
        modifier = Modifier.fillMaxWidth()
            .graphicsLayer {
                clip = true
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                shadowElevation = 8.dp.toPx()
            },
        backgroundColor = Color.White,
        elevation = 8.dp
    ) {
        BottomNavigationItem(
            icon = { },
            label = { Text(text = "Decks") },
            selected = (selectedIndex.value == 0),
            onClick = { selectedIndex.value = 0 }
        )
        BottomNavigationItem(
            icon = { },
            label = { Text(text = "Featured") },
            selected = (selectedIndex.value == 0),
            onClick = { selectedIndex.value = 0 }
        )
        BottomNavigationItem(
            icon = { },
            label = { Text(text = "Suche") },
            selected = (selectedIndex.value == 0),
            onClick = { selectedIndex.value = 0 }
        )
    }
}

expect fun getPlatformName(): String
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
            Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) {
            var cards by remember { mutableStateOf<List<Entry>>(emptyList()) }

            LaunchedEffect(Unit) {
                cards = CardDataSource.getCardPack("core")
                    .groupBy { it.type }
                    .map {
                        Entry(it.key, it.value)
                    }
            }

            InspectScreen(entries = cards) {
                scope.launch {
                    snackbarHostState.showSnackbar(it.name)
                }
            }
        }
    }
}

expect fun getPlatformName(): String
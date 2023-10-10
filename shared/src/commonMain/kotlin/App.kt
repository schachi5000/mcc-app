import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import design.compose.GameCard
import model.Card
import model.CardType
import provider.CardDataSource

@Composable
fun App() {
    MaterialTheme {
        Box(
            Modifier.fillMaxSize()
                .padding(vertical = 8.dp)
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            var cards by remember { mutableStateOf<List<Card>>(emptyList()) }

            LaunchedEffect(Unit) {
                cards = CardDataSource.getCardPack("core").filter { it.type == CardType.SIDE_SCHEME }
            }

            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    items(cards) {
                        GameCard(card = it)
                    }
                }
            )
        }
    }
}

expect fun getPlatformName(): String




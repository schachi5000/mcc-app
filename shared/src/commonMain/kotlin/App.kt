import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import design.compose.GameCard
import org.jetbrains.compose.resources.ExperimentalResourceApi

val cards = listOf("01001a", "03001a", "01003")

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(cards) {
                GameCard(cardName = it)
            }
        }
    }
}

expect fun getPlatformName(): String
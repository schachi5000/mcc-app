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
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import design.compose.GameCard
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import model.Card
import org.jetbrains.compose.resources.ExperimentalResourceApi


@OptIn(ExperimentalResourceApi::class)
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
                withContext(Dispatchers.IO) {
                    cards = getCardPack("core").filter { it.type == "hero" }
                    println("${cards.size} loaded!")
                }
            }

            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    items(cards) {
                        GameCard(cardName = it.code)
                    }
                }
            )
        }
    }
}

expect fun getPlatformName(): String

val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

suspend fun getCardPack(packCode: String) = httpClient
    .get("https://de.marvelcdb.com/api/public/cards/$packCode")
    .body<List<Card>>()

suspend fun getCard(cardCode: String) = httpClient
    .get("https://de.marvelcdb.com/api/public/card/$cardCode")
    .body<Card>()
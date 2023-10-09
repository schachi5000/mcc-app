package design.compose

import androidx.compose.foundation.Image
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    cardName: String? = null
) {
    Card(elevation = 4.dp) {
        KamelImage(
            asyncPainterResource("https://de.marvelcdb.com/bundles/cards/$cardName.png"),
            cardName
        )
    }
}
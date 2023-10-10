package design.compose

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import model.Card
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

const val PORTRAIT_RATIO = 0.715f
const val LANDSCAPE_RATIO = 1.396f

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    card: Card
) {
    Card(
        modifier = modifier.fillMaxSize().aspectRatio(PORTRAIT_RATIO),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        KamelImage(
            modifier = Modifier.fillMaxSize(),
            resource = asyncPainterResource(
                data = "https://de.marvelcdb.com/bundles/cards/${card.code}.png",
                filterQuality = FilterQuality.Medium
            ),
            contentDescription = card.name,
            animationSpec = tween(
                durationMillis = 500
            ),
            onLoading = {
                Image(
                    painter = painterResource("card_back.png"),
                    contentDescription = "Placeholder",
                    modifier = Modifier.fillMaxSize()
                )
            })
    }
}
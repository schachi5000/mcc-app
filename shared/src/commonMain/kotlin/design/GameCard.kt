package design

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import model.Card
import model.CardOrientation
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

const val PORTRAIT_RATIO = 0.715f
const val LANDSCAPE_RATIO = 1.396f


@OptIn(ExperimentalResourceApi::class, ExperimentalMaterialApi::class)
@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    card: Card,
    onClick: () -> Unit = {}
) {
    val aspectRation = when (card.orientation) {
        CardOrientation.LANDSCAPE -> LANDSCAPE_RATIO
        CardOrientation.PORTRAIT -> PORTRAIT_RATIO
    }

    Card(
        modifier = modifier.height(196.dp)
            .aspectRatio(aspectRation),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        KamelImage(
            modifier = Modifier.aspectRatio(aspectRation).scale(1.025f),
            resource = asyncPainterResource(
                data = "https://de.marvelcdb.com/bundles/cards/${card.code}.png",
                filterQuality = FilterQuality.Medium
            ),
            contentDescription = card.name,
            contentScale = ContentScale.FillBounds,
            animationSpec = tween(
                durationMillis = 1000
            ),
            onLoading = {
                Image(
                    painter = painterResource("card_back.png"),
                    contentDescription = "Placeholder",
                    modifier = Modifier.fillMaxSize(),
                )
            },
            onFailure = {
                Logger.e(throwable = it) {
                    "Failed to load image for card: $card"
                }
                Image(
                    painter = painterResource("card_back.png"),
                    contentDescription = "Placeholder",
                    modifier = Modifier.fillMaxSize().alpha(0.3f),
                )
            })
    }
}
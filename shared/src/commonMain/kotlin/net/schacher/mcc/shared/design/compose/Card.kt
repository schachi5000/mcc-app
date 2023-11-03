package net.schacher.mcc.shared.design.compose

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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardOrientation
import net.schacher.mcc.shared.model.CardType.MAIN_SCHEME
import net.schacher.mcc.shared.model.CardType.MINION
import net.schacher.mcc.shared.model.CardType.OBLIGATION
import net.schacher.mcc.shared.model.CardType.SIDE_SCHEME
import net.schacher.mcc.shared.model.CardType.TREACHERY
import net.schacher.mcc.shared.model.CardType.VILLAIN
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

const val PORTRAIT_RATIO = 0.715f
const val LANDSCAPE_RATIO = 1.396f

@OptIn(ExperimentalResourceApi::class, ExperimentalMaterialApi::class)
@Composable
fun Card(
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
                durationMillis = 500
            ),
            onLoading = {
                Image(
                    modifier = Modifier.fillMaxSize().blur(6.dp),
                    painter = painterResource(card.getLoadingResource()),
                    contentDescription = "Placeholder",
                )
            },
            onFailure = {
                Logger.e(throwable = it) {
                    "Failed to load image for card: $card"
                }
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(card.getFailureResource()),
                    contentDescription = "Placeholder",
                )
            })
    }
}

enum class BackSideColor {
    YELLOW, BLUE
}

private val Card.backSideColor: BackSideColor
    get() = when (this.type) {
        OBLIGATION,
        TREACHERY,
        MINION,
        SIDE_SCHEME,
        MAIN_SCHEME,
        VILLAIN -> BackSideColor.YELLOW

        else -> BackSideColor.BLUE
    }

private fun Card.getLoadingResource(): String = when (this.backSideColor) {
    BackSideColor.YELLOW -> "card_yellow.png"
    else -> "card_blue.png"
}

private fun Card.getFailureResource(): String = when (this.backSideColor) {
    BackSideColor.YELLOW -> "card_yellow_no_image.png"
    else -> "card_blue_no_image.png"
}
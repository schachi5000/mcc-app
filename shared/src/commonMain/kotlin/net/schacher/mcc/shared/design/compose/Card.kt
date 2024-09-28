package net.schacher.mcc.shared.design.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.card_blue_no_image
import marvelchampionscompanion.shared.generated.resources.card_yellow_no_image
import net.schacher.mcc.shared.design.theme.CardShape
import net.schacher.mcc.shared.design.theme.DefaultCardSize
import net.schacher.mcc.shared.design.theme.SleeveColors
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardOrientation
import net.schacher.mcc.shared.model.CardType.MAIN_SCHEME
import net.schacher.mcc.shared.model.CardType.MINION
import net.schacher.mcc.shared.model.CardType.OBLIGATION
import net.schacher.mcc.shared.model.CardType.SIDE_SCHEME
import net.schacher.mcc.shared.model.CardType.TREACHERY
import net.schacher.mcc.shared.model.CardType.VILLAIN
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

const val PORTRAIT_RATIO = 0.715f
const val LANDSCAPE_RATIO = 1.396f

@Composable
fun LabeledCard(
    card: Card,
    showLabel: Boolean = true,
    modifier: Modifier = Modifier.height(196.dp),
    shape: Shape = CardShape,
    onClick: () -> Unit = {}
) {
    Column {
        Card(card, modifier, shape, onClick)
        AnimatedVisibility(
            visible = showLabel,
            modifier = Modifier.padding(vertical = 8.dp)
                .sizeIn(maxWidth = 128.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = card.name,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onBackground,
                maxLines = 2,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class, ExperimentalMaterialApi::class)
@Composable
fun Card(
    card: Card,
    modifier: Modifier = Modifier.height(DefaultCardSize),
    shape: Shape = CardShape,
    onClick: () -> Unit = {}
) {
    val aspectRation = when (card.orientation) {
        CardOrientation.LANDSCAPE -> LANDSCAPE_RATIO
        CardOrientation.PORTRAIT -> PORTRAIT_RATIO
    }

    Card(
        modifier = modifier.aspectRatio(aspectRation),
        shape = shape,
        onClick = onClick
    ) {
        CardImage(
            modifier = Modifier.aspectRatio(aspectRation).scale(1.025f),
            cardCode = card.code,
            contentDescription = card.name,
            contentScale = ContentScale.FillBounds,
            animationSpec = tween(
                durationMillis = 500
            ),
            onLoading = {
                ShimmerBox(modifier = Modifier.fillMaxSize())
            },
            onFailure = {
                Logger.e { "Failed to load image for card: ${card.name}(${card.code})" }
                FailureImage(card)
            })
    }
}

@Composable
fun FailureImage(card: Card) {
    FailureImage(card.backSideColor, card.getFailureResource())
}

@Composable
fun FailureImage(backgroundColor: Color, resource: DrawableResource) {
    Image(
        modifier = Modifier.fillMaxSize()
            .background(backgroundColor, CardShape)
            .border(8.dp, backgroundColor, CardShape),
        painter = painterResource(resource),
        contentDescription = "Placeholder",
    )
}

val Card.backSideColor: Color
    get() = when (this.type) {
        OBLIGATION,
        TREACHERY,
        MINION,
        SIDE_SCHEME,
        MAIN_SCHEME,
        VILLAIN -> SleeveColors.Yellow

        else -> SleeveColors.Blue
    }

private fun Card.getFailureResource() = when (this.backSideColor) {
    SleeveColors.Yellow -> Res.drawable.card_yellow_no_image
    else -> Res.drawable.card_blue_no_image
}
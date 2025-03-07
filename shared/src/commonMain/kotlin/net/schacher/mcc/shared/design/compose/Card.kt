package net.schacher.mcc.shared.design.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import org.jetbrains.compose.resources.painterResource

const val PORTRAIT_RATIO = 0.715f
const val LANDSCAPE_RATIO = 1.396f
const val PARALLAX_DURATION_MS = 5000

@Composable
fun LabeledCard(
    card: Card,
    label: String = card.name,
    showLabel: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 2,
    modifier: Modifier = Modifier.height(196.dp),
    shape: Shape = CardShape,
    parallaxEffect: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Column {
        Card(card, modifier, shape, parallaxEffect, onClick)
        AnimatedVisibility(
            visible = showLabel,
            modifier = Modifier.padding(top = 8.dp)
                .sizeIn(maxWidth = 128.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = label,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.caption.copy(
                    shadow = Shadow(
                        color = MaterialTheme.colors.background,
                        offset = Offset.Zero,
                        blurRadius = 16.dp.value
                    ),
                ),
                maxLines = maxLines,
                minLines = minLines,
            )
        }
    }
}

@Composable
fun Card(
    card: Card,
    modifier: Modifier = Modifier.height(DefaultCardSize),
    shape: Shape = CardShape,
    parallaxEffect: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    var blur by remember { mutableStateOf(0.dp) }

    val infiniteTransition = rememberInfiniteTransition()
    val rotationX by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = PARALLAX_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val rotationY by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = -0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = PARALLAX_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val rotationZ by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = PARALLAX_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = modifier.aspectRatio(card.aspectRation)
            .applyIf(onClick != null) {
                bounceClick { state -> blur = 4.dp.takeIf { state == ButtonState.Pressed } ?: 0.dp }
            }
            .applyIf(onClick != null) {
                noRippleClickable { onClick?.invoke() }
            }
            .applyIf(parallaxEffect) {
                graphicsLayer(
                    rotationX = rotationX,
                    rotationY = rotationY,
                    rotationZ = rotationZ,
                )
            },
        shape = shape,
    ) {
        CardImage(
            modifier = Modifier.aspectRatio(card.aspectRation)
                .scale(1.025f)
                .blur(blur),
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

private val Card.aspectRation: Float
    get() = when (this.orientation) {
        CardOrientation.LANDSCAPE -> LANDSCAPE_RATIO
        CardOrientation.PORTRAIT -> PORTRAIT_RATIO
    }

private val Card.backSideColor: Color
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
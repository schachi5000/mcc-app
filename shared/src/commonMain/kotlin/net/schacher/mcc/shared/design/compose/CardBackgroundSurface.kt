package net.schacher.mcc.shared.design.compose

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.card_blue_no_image
import net.schacher.mcc.shared.design.theme.SleeveColors

@Composable
fun CardBackgroundBox(
    cardCode: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.background(MaterialTheme.colors.background)
    ) {
        CardBackgroundImage(
            modifier = Modifier.fillMaxWidth(),
            background = MaterialTheme.colors.background,
            cardCode = cardCode
        )

        content()
    }
}

@Composable
fun CardBackgroundImage(
    modifier: Modifier = Modifier.fillMaxWidth(),
    background: Color = MaterialTheme.colors.background,
    cardCode: String
) {
    Box(modifier = modifier.height(340.dp)) {
        CardImage(
            modifier = Modifier.fillMaxSize()
                .blur(30.dp)
                .background(MaterialTheme.colors.surface),
            cardCode = cardCode,
            filterQuality = FilterQuality.Low,
            contentScale = ContentScale.Crop,
            animationSpec = tween(
                durationMillis = 500
            ),
            onLoading = {},
            onFailure = {
                FailureImage(
                    backgroundColor = SleeveColors.Blue,
                    resource = Res.drawable.card_blue_no_image
                )
            })

        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to background.copy(alpha = 0f),
                        1f to background.copy(alpha = 1f)
                    )
                )
            )
        )
    }
}
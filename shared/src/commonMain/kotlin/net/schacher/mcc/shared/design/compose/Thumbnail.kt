package net.schacher.mcc.shared.design.compose

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import net.schacher.mcc.shared.design.theme.CardShape
import net.schacher.mcc.shared.design.theme.DeckShape
import net.schacher.mcc.shared.model.Card

@Composable
fun Thumbnail(card: Card, modifier: Modifier = Modifier) {
    CardImage(modifier = modifier.clip(CardShape).aspectRatio(1f).scale(2.5f)
        .graphicsLayer { translationY = 10.dp.toPx() },
        cardCode = card.code,
        filterQuality = FilterQuality.Low,
        contentDescription = card.name,
        animationSpec = tween(durationMillis = 500),
        onLoading = {
            ShimmerBox(
                Modifier.fillMaxSize(), MaterialTheme.colors.surface.copy(0.8f)
            )
        },
        onFailure = {
            Logger.e { "Failed to load image for card: ${card.name}(${card.code})" }
            Box(
                Modifier.fillMaxSize()
                    .background(MaterialTheme.colors.surface.copy(alpha = 0.8f), DeckShape)
            ) { }
        })
}
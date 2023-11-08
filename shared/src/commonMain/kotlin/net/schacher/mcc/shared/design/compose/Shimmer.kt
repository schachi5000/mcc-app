package net.schacher.mcc.shared.design.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private const val DEFAULT_DURATION_MILLIS = 2000

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colors.surface,
    shimmer: Color = if (MaterialTheme.colors.isLight) {
        Color.LightGray
    } else {
        Color.DarkGray
    },
    durationMillis: Int = DEFAULT_DURATION_MILLIS,
) {
    Box(modifier) {
        Box(modifier.fillMaxSize().background(background)) { }
        Box(modifier.fillMaxSize().shimmerBrush(shimmer, durationMillis = durationMillis)) { }
    }
}

fun Modifier.shimmerBrush(
    color: Color,
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 90f,
    durationMillis: Int = DEFAULT_DURATION_MILLIS
): Modifier = composed {
    val shimmerColors = listOf(
        color.copy(alpha = 0.0f),
        color.copy(alpha = 0.3f),
        color.copy(alpha = 0.5f),
        color.copy(alpha = 0.9f),
        color.copy(alpha = 0.5f),
        color.copy(alpha = 0.3f),
        color.copy(alpha = 0.0f),
    )

    val transition = rememberInfiniteTransition()
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Shimmer loading animation",
    )

    this.background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
            end = Offset(x = translateAnimation.value, y = angleOfAxisY),
        )
    )
}

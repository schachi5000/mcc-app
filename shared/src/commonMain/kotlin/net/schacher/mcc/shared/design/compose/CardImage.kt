package net.schacher.mcc.shared.design.compose

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import co.touchlab.kermit.Logger
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import pro.schacher.mcc.BuildConfig

@Composable
fun CardImage(
    cardCode: String,
    filterQuality: FilterQuality = FilterQuality.High,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    onLoading: @Composable (BoxScope.(Float) -> Unit)? = null,
    onFailure: @Composable (BoxScope.(Throwable) -> Unit)? = null,
    contentAlignment: Alignment = Alignment.Center,
    animationSpec: FiniteAnimationSpec<Float>? = null,
) {
    KamelImage(
        resource = asyncPainterResource(
            data = "${BuildConfig.SERVICE_URL}/cards/${cardCode}/image",
            filterQuality = filterQuality
        ),
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        onLoading = onLoading,
        onFailure = onFailure,
        contentAlignment = contentAlignment,
        animationSpec = animationSpec,
    )
}
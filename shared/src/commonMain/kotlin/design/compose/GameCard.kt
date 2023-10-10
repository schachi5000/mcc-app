package design.compose

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    cardName: String? = null
) {
    Card(
        modifier = modifier.fillMaxSize(),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        KamelImage(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            resource = asyncPainterResource(
                data = "https://de.marvelcdb.com/bundles/cards/$cardName.png",
                filterQuality = FilterQuality.Medium
            ),
            contentDescription = cardName,
            animationSpec = tween(),
            onLoading = {
                LoadingCard()
            }
        )
    }
}

@Composable
fun LoadingCard() {
    Box(
        modifier = Modifier
            .size(
                width = with(LocalDensity.current) { 300.toDp() },
                height = with(LocalDensity.current) { 418.toDp() })
            .background(color = Color.LightGray),
    )
}
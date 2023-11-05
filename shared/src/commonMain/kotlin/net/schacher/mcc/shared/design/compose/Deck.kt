package net.schacher.mcc.shared.design.compose

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import net.schacher.mcc.shared.design.theme.CardShape
import net.schacher.mcc.shared.design.theme.DeckShape
import net.schacher.mcc.shared.design.theme.color
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

private val deckHeight = 120.dp
private val contentPadding = 8.dp

@Composable
fun Deck(deck: Deck, onClick: () -> Unit = {}) {

    Box {
        BackgroundImage(
            modifier = Modifier.fillMaxSize().height(deckHeight),
            deck = deck,
        )
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(deckHeight)
                .clickable { onClick() }
                .padding(contentPadding)
        ) {
            Thumbnail(
                modifier = Modifier.fillMaxHeight(),
                card = deck.heroCard
            )
            Spacer(Modifier.width(contentPadding))
            Column(
                modifier = Modifier.fillMaxHeight()
                    .background(MaterialTheme.colors.surface.copy(alpha = 0.8f), DeckShape)
                    .padding(12.dp)
            ) {
                Text(
                    text = deck.name,
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {

                    InfoRow("${deck.cards.size} Karten", "ic_cards.xml")
                    Spacer(Modifier.height(4.dp))
                    InfoRow("${deck.requiredDecksCount} Packs benötigt", "ic_deck.xml")
                }
            }
        }
    }
}

@Composable
private fun BackgroundImage(modifier: Modifier = Modifier, deck: Deck) {
    Box(
        modifier = modifier.fillMaxSize().clip(DeckShape)
    ) {
        KamelImage(
            modifier = Modifier.fillMaxSize()
                .blur(6.dp)
                .background(MaterialTheme.colors.surface)
                .graphicsLayer {
                    scaleX = 1.6f
                    scaleY = 1.6f
                },
            resource = asyncPainterResource(
                data = "https://de.marvelcdb.com/bundles/cards/${deck.heroCard.code}.png",
                filterQuality = FilterQuality.Medium,
            ),
            contentDescription = deck.name,
            contentScale = ContentScale.Crop,
            animationSpec = tween(
                durationMillis = 500
            ),
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }),
            onLoading = {},
            onFailure = {})
        Column(
            modifier = modifier.fillMaxSize()
                .clip(DeckShape)
                .background(deck.aspect.color.copy(alpha = 0.4f), DeckShape)
        ) {}
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun Thumbnail(modifier: Modifier = Modifier, card: Card) {
    KamelImage(
        modifier = modifier.clip(CardShape)
            .aspectRatio(1f)
            .scale(1.7f)
            .graphicsLayer { translationY = 20.dp.toPx() },
        resource = asyncPainterResource(
            data = "https://de.marvelcdb.com/bundles/cards/${card.code}.png",
            filterQuality = FilterQuality.Medium
        ),
        contentDescription = card.name,
        contentScale = ContentScale.Crop,

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
            Logger.e(throwable = it) { "Failed to load image for card: $card" }
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(card.getFailureResource()),
                contentDescription = "Placeholder",
            )
        })
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun InfoRow(label: String, iconResource: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(iconResource),
            contentDescription = null,
            Modifier.size(16.dp)
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = label,
            maxLines = 1,
        )
    }
}

package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.theme.getColor
import net.schacher.mcc.shared.model.Deck
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun Deck(deck: Deck, onClick: () -> Unit = {}) {
    Column {
        DeckStack(modifier = Modifier.fillMaxWidth(), deck.aspect?.getColor() ?: Color.LightGray)
        Spacer(Modifier.height(1.dp))
        Row(
            modifier = Modifier.wrapContentHeight()
                .fillMaxWidth()
                .height(128.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.surface)
                .clickable { onClick() }
        ) {
            Card(card = deck.heroCard)

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = deck.name,
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))
                InfoRow("${deck.cards.size} Karten", "ic_cards.xml")
                Spacer(Modifier.height(4.dp))
                InfoRow("${deck.requiredDecksCount} Packs benötigt", "ic_deck.xml")
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun InfoRow(label: String, iconResource: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(iconResource), contentDescription = null, Modifier.size(16.dp))
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = label,
            maxLines = 1,
        )
    }
}

@Composable
private fun DeckStack(modifier: Modifier = Modifier, color: Color) {
    val shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
    val backgroundColor = Color.White.copy(alpha = 0.75f)

    Column(modifier) {
        Box(Modifier.height(5.dp).padding(horizontal = 16.dp)) {
            Row(Modifier.fillMaxSize().background(backgroundColor, shape)) { }
            Row(Modifier.fillMaxSize().alpha(0.2f).background(color, shape)) { }
        }
        Spacer(Modifier.height(1.dp))
        Box(Modifier.height(5.dp).padding(horizontal = 8.dp)) {
            Row(Modifier.fillMaxSize().background(backgroundColor, shape)) { }
            Row(Modifier.fillMaxSize().alpha(0.4f).background(color, shape)) { }
        }
    }
}
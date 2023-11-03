package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.theme.getColor
import net.schacher.mcc.shared.model.Deck

@Composable
fun Deck(deck: Deck) {
    Column {
        DeckStack(modifier = Modifier.fillMaxWidth(), deck.aspect?.getColor() ?: Color.LightGray)
        Spacer(Modifier.height(1.dp))
        Row(
            modifier = Modifier.wrapContentHeight()
                .fillMaxWidth()
                .height(128.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.surface)
        ) {
            Card(card = deck.heroCard)

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = deck.name,
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DeckStack(modifier: Modifier = Modifier, color: Color) {
    Column(modifier) {
        Row(
            Modifier.height(5.dp)
                .padding(horizontal = 16.dp)
                .alpha(0.2f)
                .background(
                    color,
                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
                .fillMaxWidth()
        ) { }
        Spacer(Modifier.height(1.dp))
        Row(
            Modifier.height(5.dp)
                .padding(horizontal = 8.dp)
                .alpha(0.3f)
                .background(
                    color,
                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
                .fillMaxWidth()
        ) { }
    }
}
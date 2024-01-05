package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.model.Deck

private val deckHeight = 128.dp

@Composable
fun Deck(deck: Deck, onClick: (Deck) -> Unit = {}) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    Column(modifier = Modifier) {
        Column(
            Modifier.height(4.dp).width(size.width.dp)
                .background(Color.Red, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        ) {}
        Box(
            Modifier.height(4.dp).width(size.width.dp)
                .background(Color.Red, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        ) {}

        Card(
            modifier = Modifier.height(deckHeight)
                .onGloballyPositioned { coordinates -> size = coordinates.size },
            card = deck.hero
        ) {
            onClick(deck)
        }
    }
}
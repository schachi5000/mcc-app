package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.theme.DeckShape
import net.schacher.mcc.shared.model.Deck

private val deckHeight = 128.dp

@Composable
fun Deck(modifier: Modifier = Modifier, deck: Deck, onClick: (Deck) -> Unit = {}) {
    var componentWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(
        modifier = modifier.wrapContentHeight(),
    ) {
        Card(
            modifier = Modifier.align(Alignment.TopCenter)
                .width(componentWidth - 22.dp),
            card = deck.cards[2],
            shape = DeckShape
        ) {}

        Card(
            modifier = Modifier.padding(top = 6.dp)
                .align(Alignment.TopCenter)
                .width(componentWidth - 10.dp),
            card = deck.cards[1],
            shape = DeckShape
        ) {}

        Card(
            modifier = Modifier.padding(top = 12.dp)
                .width(deckHeight)
                .onGloballyPositioned {
                    componentWidth = with(density) {
                        it.size.width.toDp()
                    }
                },
            card = deck.hero,
            shape = DeckShape
        ) {
            onClick(deck)
        }
    }
}
package net.schacher.mcc.shared.design

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import net.schacher.mcc.shared.model.Deck

@Composable
fun GameDeck(deck: Deck) {
    Row {
        GameCard(card = deck.heroCard)
    }
}
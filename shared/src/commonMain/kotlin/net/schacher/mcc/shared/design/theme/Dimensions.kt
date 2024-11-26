package net.schacher.mcc.shared.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.theme.CornerRadius.Card
import net.schacher.mcc.shared.design.theme.CornerRadius.Deck
import net.schacher.mcc.shared.design.theme.CornerRadius.Default

object CornerRadius {
    val Card = 16.dp
    val Deck = 16.dp
    val Default = 16.dp
}

val ContentPadding = 16.dp

val FABPadding = ContentPadding * 2

val DefaultCardSize = 216.dp

val ButtonSize = 56.dp

val CardShape = RoundedCornerShape(Card)

val DeckShape = RoundedCornerShape(Deck)

val DefaultShape = RoundedCornerShape(Default)

val BottomSheetShape = RoundedCornerShape(topStart = Default, topEnd = Default)

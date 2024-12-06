package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.model.Card

@Composable
fun CardRow(modifier: Modifier, cardRowEntry: CardRowEntry, onCardSelected: (Card) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderSmall(
                title = cardRowEntry.title,
                subTitle = cardRowEntry.cards.size.toString()
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cardRowEntry.cards.count()) {
                if (it == 0) {
                    Spacer(Modifier.size(ContentPadding))
                }
                Column {
                    LabeledCard(
                        card = cardRowEntry.cards[it],
                        minLines = 2
                    ) {
                        onCardSelected(cardRowEntry.cards[it])
                    }
                }

                if (it == cardRowEntry.cards.lastIndex) {
                    Spacer(Modifier.size(ContentPadding))
                }
            }
        }
        Spacer(Modifier.size(16.dp))
    }
}

data class CardRowEntry(val title: String, val cards: List<Card>)
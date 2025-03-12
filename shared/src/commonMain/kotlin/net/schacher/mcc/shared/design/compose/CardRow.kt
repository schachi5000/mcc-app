package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.design.theme.DefaultHorizontalArrangement
import net.schacher.mcc.shared.model.Card

@Composable
fun CardRow(modifier: Modifier, cardRowEntry: CardRowEntry, onCardSelected: (Card) -> Unit) {
    Column(modifier = modifier.fillMaxWidth().wrapContentHeight()) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(
                    start = ContentPadding,
                    bottom = ContentPadding,
                    end = ContentPadding
                ),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderSmall(
                title = cardRowEntry.title,
                subTitle = cardRowEntry.cards.size.toString()
            )
        }

        LazyRow(horizontalArrangement = DefaultHorizontalArrangement) {
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
            }
        }
    }
}

data class CardRowEntry(val title: String, val cards: List<Card>)
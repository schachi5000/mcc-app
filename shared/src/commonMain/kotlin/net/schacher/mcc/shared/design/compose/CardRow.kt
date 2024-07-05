package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            Text(
                modifier = Modifier.alignByBaseline(),
                text = cardRowEntry.title.uppercase(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onBackground,
                fontSize = 28.sp
            )

            Text(
                modifier = Modifier.alignByBaseline(),
                text = "${cardRowEntry.cards.size}",
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.75f),
                fontSize = 17.sp
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
                    Card(card = cardRowEntry.cards[it]) {
                        onCardSelected(cardRowEntry.cards[it])
                    }
                    Text(
                        modifier = Modifier.padding(top = 8.dp)
                            .sizeIn(maxWidth = 128.dp)
                            .align(Alignment.CenterHorizontally),
                        text = "${cardRowEntry.cards[it].name}\n",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onBackground,
                        maxLines = 2,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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
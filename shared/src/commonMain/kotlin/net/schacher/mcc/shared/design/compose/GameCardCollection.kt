package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
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
import net.schacher.mcc.shared.model.Card

@Composable
fun InspectScreen(
    modifier: Modifier = Modifier,
    cards: List<Card>,
    onCardClicked: (Card) -> Unit = {}
) {
    val entries = cards.groupBy { it.type }
        .map { Entry("${it.key} (${it.value.size})", it.value) }

    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(entries.count()) { item ->
            if (item == 0) {
                Spacer(Modifier.statusBarsPadding())
            }

            EntryRow(
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = if (item == 0) 0.dp else 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
                entry = entries[item]
            ) {
                onCardClicked(it)
            }
        }
    }
}

@Composable
fun EntryRow(modifier: Modifier, entry: Entry, onCardSelected: (Card) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.alignByBaseline(),
                text = entry.title.uppercase(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onBackground,
                fontSize = 28.sp
            )

            Text(
                modifier = Modifier.alignByBaseline(),
                text = "${entry.cards.size}",
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.75f),
                fontSize = 17.sp
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(entry.cards.count()) {
                if (it == 0) {
                    Spacer(Modifier.size(16.dp))
                }
                Column {
                    Card(card = entry.cards[it]) {
                        onCardSelected(entry.cards[it])
                    }
                    Text(
                        modifier = Modifier.padding(top = 8.dp)
                            .sizeIn(maxWidth = 128.dp)
                            .align(Alignment.CenterHorizontally),
                        text = "${entry.cards[it].name}\n",
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (it == entry.cards.lastIndex) {
                    Spacer(Modifier.size(16.dp))
                }
            }
        }
        Spacer(Modifier.size(16.dp))
    }
}

data class Entry(val title: String, val cards: List<Card>)
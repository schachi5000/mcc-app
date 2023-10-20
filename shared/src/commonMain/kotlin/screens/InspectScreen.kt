package screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import design.GameCard
import model.Card

@Composable
fun InspectScreen(
    modifier: Modifier = Modifier,
    entries: List<Entry>,
    onCardSelected: (Card) -> Unit = {}
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(entries.count()) { item ->
            if (item == 0) {
                Spacer(Modifier.statusBarsPadding())
            }

            EntryRow(entries[item]) {
                onCardSelected(it)
            }
        }
    }
}

@Composable
fun EntryRow(entry: Entry, onCardSelected: (Card) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = entry.title.toUpperCase(Locale.current),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )
        LazyRow(

            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(entry.cards.count()) {
                if (it == 0) {
                    Spacer(Modifier.size(16.dp))
                }
                GameCard(card = entry.cards[it]) {
                    onCardSelected(entry.cards[it])
                }
                if (it == entry.cards.lastIndex) {
                    Spacer(Modifier.size(16.dp))
                }
            }
        }
        Spacer(Modifier.size(24.dp))
    }
}

data class Entry(val title: String, val cards: List<Card>)
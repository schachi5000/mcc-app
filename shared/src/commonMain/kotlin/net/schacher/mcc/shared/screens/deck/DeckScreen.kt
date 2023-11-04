package net.schacher.mcc.shared.screens.deck

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.compose.Deck
import net.schacher.mcc.shared.design.theme.DeckShape
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.screens.deck.ListItem.DeckItem

@Composable
fun DeckScreen(
    deckViewModel: DeckViewModel,
    onDeckClick: (Deck) -> Unit,
    onAddDeckClick: () -> Unit
) {
    val state by deckViewModel.state.collectAsState()
    val entries = state.decks.map { DeckItem(it) }
        .toMutableList()
        .also { it.add(ListItem.AddDeck) }

    Box(
        modifier = Modifier.fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn {
            items(entries.size) { index ->
                if (index == 0) {
                    Spacer(Modifier.statusBarsPadding().height(16.dp))
                }

                when (val entry = entries[index]) {
                    is DeckItem -> Deck(entry.deck) {
                        onDeckClick(entry.deck)
                    }

                    is ListItem.AddDeck -> AddDeckButton { onAddDeckClick() }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AddDeckButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(128.dp)
            .fillMaxWidth()
            .border(BorderStroke(2.dp, Color.LightGray), DeckShape),
        shape = DeckShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
                    .align(Alignment.CenterHorizontally),
                tint = MaterialTheme.colors.onSurface,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Deck hinzufügen",
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

private sealed interface ListItem {
    data class DeckItem(val deck: Deck) : ListItem
    object AddDeck : ListItem
}
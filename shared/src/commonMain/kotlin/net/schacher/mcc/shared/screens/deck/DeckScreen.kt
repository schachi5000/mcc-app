package net.schacher.mcc.shared.screens.deck

import IS_ANDROID
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.compose.CardRow
import net.schacher.mcc.shared.design.compose.CardRowEntry
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.model.Deck

@Composable
fun DeckScreen(
    deck: Deck,
    onCloseClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)
    ) {
        val entries = deck.cards.groupBy { it.type }
            .map { CardRowEntry("${it.key} (${it.value.size})", it.value) }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(entries.count()) { index ->
                if (index == 0) {
                    Spacer(Modifier.statusBarsPadding().height(16.dp))
                }

                CardRow(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = if (index == 0) 0.dp else 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    cardRowEntry = entries[index]
                ) {
                    // onCardClicked(it)
                }
            }
        }

        FloatingActionButton(
            onClick = onCloseClick,
            modifier = Modifier.align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(
                    start = 16.dp,
                    bottom = if (IS_ANDROID) 16.dp else 0.dp
                ),
            shape = DefaultShape
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Close"
            )
        }
    }
}



package net.schacher.mcc.shared.screens.spotlight

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.no_decks_found
import marvelchampionscompanion.shared.generated.resources.today
import marvelchampionscompanion.shared.generated.resources.two_days_ago
import marvelchampionscompanion.shared.generated.resources.yesterday
import net.schacher.mcc.shared.design.compose.DeckListItem
import net.schacher.mcc.shared.design.compose.LoadingDeckListItem
import net.schacher.mcc.shared.design.compose.ShimmerBox
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.screens.main.topInset
import net.schacher.mcc.shared.screens.spotlight.ListItem.DeckItem
import net.schacher.mcc.shared.screens.spotlight.ListItem.HeaderItem
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SpotlightScreen(
    viewModel: SpotlightViewModel = koinInject(),
    topInset: Dp,
    onDeckClick: (Deck) -> Unit
) {
    val state by viewModel.state.collectAsState()

    SpotlightScreen(state, topInset, onDeckClick)
}

@Composable
fun SpotlightScreen(
    state: SpotlightViewModel.UiState,
    topInset: Dp = 0.dp,
    onDeckClick: (Deck) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(horizontal = ContentPadding)
    ) {

        AnimatedVisibility(
            visible = !state.loading,
            exit = fadeOut(),
            enter = fadeIn()
        ) {
            val entries = mutableListOf<ListItem>()
            state.decks.forEach { (date, decks) ->
                entries.add(HeaderItem(getLabelByDate(date)))
                decks.forEach { deck ->
                    entries.add(DeckItem(deck))
                }
            }

            LazyColumn {
                items(entries.size) { index ->
                    if (index == 0) {
                        Spacer(Modifier.statusBarsPadding().height(topInset))
                    }

                    when (val entry = entries[index]) {
                        is HeaderItem -> Header(entry.header)
                        is DeckItem -> DeckListItem(deck = entry.deck) {
                            onDeckClick(entry.deck)
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }

        if (!state.loading && state.decks.isEmpty()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(Res.string.no_decks_found),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
            )
        }

        AnimatedVisibility(
            visible = state.loading,
            exit = fadeOut(),
            enter = fadeIn()
        ) {
            LoadingContent()
        }
    }
}

@Composable
private fun getLabelByDate(date: LocalDate): String {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfYear

    return when (date.dayOfYear) {
        today -> stringResource(Res.string.today)
        today - 1 -> stringResource(Res.string.yesterday)
        today - 2 -> stringResource(Res.string.two_days_ago)
        else -> "${date.dayOfMonth}. ${date.month}"
    }
}

@Composable
private fun Header(label: String) {
    Row(
        modifier = Modifier.padding(top = 16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = label,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground,
        )
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Spacer(Modifier.statusBarsPadding().height(topInset + 14.dp))

        ShimmerBox(
            modifier = Modifier
                .width(80.dp)
                .height(32.dp)
                .clip(DefaultShape)
        )

        Spacer(Modifier.height(28.dp))

        for (i in 0..6) {
            LoadingDeckListItem()
            Spacer(Modifier.height(32.dp))
        }
    }
}


private sealed interface ListItem {
    data class DeckItem(val deck: Deck) : ListItem
    data class HeaderItem(val header: String) : ListItem
}
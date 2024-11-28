package net.schacher.mcc.shared.screens.spotlight

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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import marvelchampionscompanion.shared.generated.resources.spotlight
import marvelchampionscompanion.shared.generated.resources.today
import marvelchampionscompanion.shared.generated.resources.two_days_ago
import marvelchampionscompanion.shared.generated.resources.yesterday
import net.schacher.mcc.shared.design.compose.DeckListItem
import net.schacher.mcc.shared.design.compose.LoadingDeckListItem
import net.schacher.mcc.shared.design.compose.Header
import net.schacher.mcc.shared.design.compose.HeaderSmall
import net.schacher.mcc.shared.design.compose.ShimmerBox
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.screens.spotlight.ListItem.DeckItem
import net.schacher.mcc.shared.screens.spotlight.ListItem.HeaderItem
import net.schacher.mcc.shared.screens.spotlight.ListItem.LoadingItem
import net.schacher.mcc.shared.screens.spotlight.ListItem.TopHeaderItem
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SpotlightScreen(
    viewModel: SpotlightViewModel = koinViewModel(),
    topInset: Dp = ContentPadding,
    onDeckClick: (Deck) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    SpotlightScreen(state, topInset, onDeckClick, viewModel::onRefresh)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SpotlightScreen(
    state: SpotlightViewModel.UiState,
    topInset: Dp = 0.dp,
    onDeckClick: (Deck) -> Unit,
    onRefresh: () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(state.loading, { onRefresh() })

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = ContentPadding)
            .pullRefresh(pullRefreshState)
    ) {
        val entries = mutableListOf<ListItem>(TopHeaderItem)
        if (state.loading) {
            entries.add(LoadingItem)
        } else {
            state.decks.forEach { (date, decks) ->
                entries.add(HeaderItem(getLabelByDate(date)))
                decks.forEach { deck ->
                    entries.add(DeckItem(deck))
                }
            }
        }

        LazyColumn {
            items(entries.size) { index ->
                when (val entry = entries[index]) {
                    is TopHeaderItem -> {
                        Spacer(Modifier.statusBarsPadding().height(topInset))
                        Header(stringResource(Res.string.spotlight))
                        Spacer(Modifier.height(ContentPadding))
                    }

                    is LoadingItem -> {
                        LoadingContent()
                    }

                    is HeaderItem -> {
                        HeaderSmall(entry.header)
                        Spacer(Modifier.height(ContentPadding))
                    }

                    is DeckItem -> {
                        DeckListItem(deck = entry.deck) {
                            onDeckClick(entry.deck)
                        }
                        Spacer(Modifier.height(ContentPadding * 2))
                    }
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

        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter)
                .statusBarsPadding(),
            refreshing = state.loading,
            state = pullRefreshState,
            contentColor = MaterialTheme.colors.onPrimary,
            backgroundColor = MaterialTheme.colors.primary
        )
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
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = label,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onBackground,
        )
    }
}

@Composable
private fun LoadingContent() {
    Column(modifier = Modifier.fillMaxSize()) {
        ShimmerBox(
            modifier = Modifier
                .width(80.dp)
                .height(32.dp)
                .clip(DefaultShape)
        )

        Spacer(Modifier.height(20.dp))

        for (i in 0..6) {
            LoadingDeckListItem()
            Spacer(Modifier.height(32.dp))
        }
    }
}


private sealed interface ListItem {
    data class DeckItem(val deck: Deck) : ListItem
    data class HeaderItem(val header: String) : ListItem
    data object TopHeaderItem : ListItem
    data object LoadingItem : ListItem
}
package net.schacher.mcc.shared.screens.mydecks

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.my_decks
import marvelchampionscompanion.shared.generated.resources.no_decks_found
import net.schacher.mcc.shared.design.compose.BackButton
import net.schacher.mcc.shared.design.compose.DeckListItem
import net.schacher.mcc.shared.design.compose.MainHeader
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.screens.mydecks.ListItem.DeckItem
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MyDecksScreen(
    viewModel: MyDecksViewModel = koinViewModel(),
    topInset: Dp = ContentPadding,
    onDeckClick: (Deck) -> Unit,
    onAddDeckClick: () -> Unit = {},
    onBackPress: (() -> Unit)? = null
) {
    val state by viewModel.state.collectAsState()

    MyDecksScreen(
        state = state,
        topInset = topInset,
        onDeckClick = onDeckClick,
        onAddDeckClick = onAddDeckClick,
        onRefresh = { viewModel.onRefreshClicked() },
        onBackPress = onBackPress
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyDecksScreen(
    state: MyDecksViewModel.UiState,
    topInset: Dp = 0.dp,
    onRefresh: () -> Unit,
    onDeckClick: (Deck) -> Unit,
    onAddDeckClick: () -> Unit,
    onBackPress: (() -> Unit)? = null
) {
    val entries = mutableListOf<ListItem>().also {
        it.addAll(state.decks.map { DeckItem(it) })
    }

    var expanded by remember { mutableStateOf(false) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                expanded = if (expanded) {
                    available.y > -10
                } else {
                    available.y > 1
                }

                return Offset.Zero
            }
        }
    }

    val pullRefreshState = rememberPullRefreshState(state.refreshing, { onRefresh() })

    Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
        if (state.decks.isEmpty() && !state.refreshing) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(Res.string.no_decks_found),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(start = ContentPadding)
                .nestedScroll(nestedScrollConnection)
        ) {
            items(entries.size) { index ->
                if (index == 0) {
                    Spacer(Modifier.statusBarsPadding().height(topInset))
                    MainHeader(stringResource(Res.string.my_decks))
                    Spacer(Modifier.height(ContentPadding))
                }

                when (val entry = entries[index]) {
                    is DeckItem -> DeckListItem(entry.deck) {
                        onDeckClick(entry.deck)
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }

        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding(),
            refreshing = state.refreshing,
            state = pullRefreshState,
            contentColor = MaterialTheme.colors.onPrimary,
            backgroundColor = MaterialTheme.colors.primary
        )

        onBackPress?.let {
            BackButton(it)
        }
    }
}

@Composable
fun animateHorizontalAlignmentAsState(targetBiasValue: Float): State<BiasAlignment.Horizontal> {
    val bias by animateFloatAsState(targetBiasValue)
    return derivedStateOf { BiasAlignment.Horizontal(bias) }
}

private sealed interface ListItem {
    data class DeckItem(val deck: Deck) : ListItem
}
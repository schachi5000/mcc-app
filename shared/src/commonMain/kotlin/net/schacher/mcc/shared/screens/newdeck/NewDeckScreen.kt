package net.schacher.mcc.shared.screens.newdeck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.select_hero
import net.schacher.mcc.shared.design.compose.BackButton
import net.schacher.mcc.shared.design.compose.BottomSpacer
import net.schacher.mcc.shared.design.compose.GridItem
import net.schacher.mcc.shared.design.compose.Header
import net.schacher.mcc.shared.design.compose.maxSpanItem
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardOrientation
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val GRID_CELL_SIZE = 3

@Composable
fun NewDeckScreen(
    viewModel: NewDeckViewModel = koinViewModel(),
    onNewDeckSelected: (Card, Aspect?) -> Unit,
    onBackPress: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    NewDeckScreen(
        state = state,
        onCardSelected = { viewModel.onHeroCardSelected(it) },
        onBackPress = { onBackPress() }
    )
}

@Composable
fun NewDeckScreen(
    state: NewDeckViewModel.UiState,
    onCardSelected: (Card) -> Unit,
    onBackPress: () -> Unit
) {
    val decks = state.allHeroes
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize().padding(horizontal = ContentPadding),
            columns = GridCells.Fixed(GRID_CELL_SIZE),
            horizontalArrangement = Arrangement.spacedBy(ContentPadding),
        ) {
            maxSpanItem {
                Row(Modifier.statusBarsPadding().padding(vertical = ContentPadding)) {
                    Spacer(Modifier.height(ContentPadding))
                    Header(stringResource(Res.string.select_hero))
                    Spacer(Modifier.height(ContentPadding))
                }
            }

            items(decks.size) {
                val card = decks[it]

                GridItem(
                    card = card,
                    title = card.name,
                    iconOffset = getOffset(card),
                    modifier = Modifier.padding(bottom = 8.dp),
                ) {
                    onCardSelected(card)
                }
            }

            maxSpanItem {
                BottomSpacer()
            }
        }

        BackButton(onBackPress)
    }
}

private fun getOffset(card: Card): DpOffset = if (card.orientation == CardOrientation.PORTRAIT) {
    DpOffset(0.dp, 15.dp)
} else {
    DpOffset(5.dp, 5.dp)
}
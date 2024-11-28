package net.schacher.mcc.shared.screens.newdeck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.spotlight
import net.schacher.mcc.shared.design.compose.BackButton
import net.schacher.mcc.shared.design.compose.BottomSpacer
import net.schacher.mcc.shared.design.compose.CardListItem
import net.schacher.mcc.shared.design.compose.Header
import net.schacher.mcc.shared.design.compose.ListItem
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = ContentPadding)
        ) {
            item {
                Spacer(Modifier.statusBarsPadding().height(ContentPadding))
                Header("Select a hero deck")
                Spacer(Modifier.height(ContentPadding))
            }
            items(decks.size) { index ->
                ListItem(decks[index], decks[index].name) {
                    onCardSelected(decks[index])
                }

                Spacer(Modifier.height(ContentPadding * 2))
            }

            item {
                BottomSpacer()
            }
        }

        BackButton(onBackPress)
    }
}
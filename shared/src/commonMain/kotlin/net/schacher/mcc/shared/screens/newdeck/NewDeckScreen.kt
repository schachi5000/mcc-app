package net.schacher.mcc.shared.screens.newdeck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.compose.BackButton
import net.schacher.mcc.shared.design.compose.LabeledCard
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import org.koin.compose.koinInject

@Composable
fun NewDeckScreen(
    viewModel: NewDeckViewModel = koinInject(),
    onNewDeckSelected: (Card, Aspect?) -> Unit,
    onBackPress: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    NewDeckScreen(
        state = state,
        onCardSelected = { viewModel.onHeroCardSelected(it) },
        onBackPress = { onBackPress() },
        onNewDeckSelected = onNewDeckSelected,
        onDialogDismissed = { viewModel.onBackPress() }
    )
}

@Composable
fun NewDeckScreen(
    state: NewDeckViewModel.UiState,
    onCardSelected: (Card) -> Unit,
    onBackPress: () -> Unit,
    onNewDeckSelected: (Card, Aspect?) -> Unit,
    onDialogDismissed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colors.onBackground,
                text = "Select a hero",
                style = MaterialTheme.typography.h3
            )

            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.allHeroes.forEach { card ->
                    item {
                        LabeledCard(
                            card = card,
                            onClick = { onNewDeckSelected(card, null) }
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(72.dp))
                }
            }
        }

        BackButton(onBackPress)
    }
}
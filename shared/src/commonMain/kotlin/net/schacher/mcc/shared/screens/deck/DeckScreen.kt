package net.schacher.mcc.shared.screens.deck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.schacher.mcc.shared.design.compose.BackButton
import net.schacher.mcc.shared.design.compose.BottomSpacer
import net.schacher.mcc.shared.design.compose.Card
import net.schacher.mcc.shared.design.compose.CardBackgroundBox
import net.schacher.mcc.shared.design.compose.CardRow
import net.schacher.mcc.shared.design.compose.CardRowEntry
import net.schacher.mcc.shared.design.compose.ConfirmationDialog
import net.schacher.mcc.shared.design.theme.ButtonSize
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.design.theme.FABPadding
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.platform.isAndroid
import net.schacher.mcc.shared.repositories.DeckRepository
import org.koin.compose.koinInject


@Composable
fun DeckScreen(
    deckId: Int,
    deckRepository: DeckRepository = koinInject(),
    navController: NavController = koinInject(),
    onDeleteDeckClick: (Int) -> Unit,
) {
    val deck = deckRepository.getDeckById(deckId) ?: return

    DeckScreen(
        deck = deck,
        navController = navController,
        onDeleteDeckClick = onDeleteDeckClick,
    )
}

@Composable
fun DeckScreen(
    deck: Deck,
    navController: NavController,
    onDeleteDeckClick: (Int) -> Unit
) {
    var deleteDeckShowing by remember { mutableStateOf(false) }

    Content(
        deck = deck,
        onCloseClick = { navController.popBackStack() },
        onDeleteDeckClick = { deleteDeckShowing = true },
        onCardClick = {
            navController.navigate("card/${it.code}")
        }
    )

    if (deleteDeckShowing) {
        ConfirmationDialog(
            title = "Delete deck",
            message = "Are you sure you want to delete this deck?",
            onConfirm = {
                deleteDeckShowing = false
                onDeleteDeckClick(deck.id)
            },
            onDismiss = { deleteDeckShowing = false }
        )
    }
}

@Composable
private fun Content(
    deck: Deck,
    onCloseClick: () -> Unit,
    onDeleteDeckClick: (Int) -> Unit,
    onCardClick: (Card) -> Unit
) {
    CardBackgroundBox(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        cardCode = deck.hero.code,
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Spacer(Modifier.statusBarsPadding().height(ContentPadding))
            }
            item {
                Row(
                    modifier = Modifier.padding(
                        vertical = 16.dp,
                        horizontal = ContentPadding
                    )
                ) {
                    Card(deck.hero) {
                        onCardClick(deck.hero)
                    }
                }
            }

            val heroCards = CardRowEntry("Hero cards", deck.cards
                .filter { it.type != CardType.HERO && it.setCode == deck.hero.setCode }
                .distinctBy { it.name }
                .sortedBy { it.cost ?: 0 })

            item {
                CardRow(
                    modifier = Modifier.padding(
                        horizontal = ContentPadding,
                        vertical = 16.dp,
                    ),
                    cardRowEntry = heroCards
                ) {
                    onCardClick(it)
                }
            }
            val otherCards = CardRowEntry("Other cards", deck.cards
                .filter { it.setCode != deck.hero.setCode }
                .distinctBy { it.name }
                .sortedBy { it.cost ?: 0 })

            item {
                CardRow(
                    modifier = Modifier.padding(
                        horizontal = ContentPadding,
                        vertical = 16.dp,
                    ),
                    cardRowEntry = otherCards
                ) {
                    onCardClick(it)
                }
            }

            item {
                BottomSpacer()
            }
        }

        BackButton(onCloseClick)

        FloatingActionButton(
            onClick = { onDeleteDeckClick(deck.id) },
            modifier = Modifier.align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(
                    end = FABPadding,
                    bottom = if (isAndroid()) FABPadding else 0.dp
                )
                .size(ButtonSize),
            contentColor = MaterialTheme.colors.onPrimary,
            backgroundColor = MaterialTheme.colors.primary,
            shape = DefaultShape
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Delete"
            )
        }
    }
}

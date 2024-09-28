package net.schacher.mcc.shared.screens.deck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
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
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType
import net.schacher.mcc.shared.model.Deck
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
    )
}

@Composable
fun DeckScreen(
    deck: Deck,
    navController: NavController
) {
    Content(
        deck = deck,
        onCloseClick = { navController.popBackStack() },
        onCardClick = {
            navController.navigate("card/${it.code}")
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Content(
    deck: Deck,
    onCloseClick: () -> Unit,
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

            var columnCount = 3
            val rows = otherCards.cards.chunked(columnCount)
            items(rows.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = ContentPadding,
                            vertical = 8.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rows[index].forEach { card ->
                        Box(Modifier.weight(1f)) {
                            Card(
                                modifier = Modifier.wrapContentHeight(),
                                card = card
                            ) {
                                onCardClick(card)
                            }

                            IconButton(
                                modifier = Modifier.padding(8.dp)
                                    .align(Alignment.TopEnd),
                                onClick = {},
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Card Options"
                                )
                            }
                        }
                    }

                    if (rows.size < columnCount) {
                        repeat(columnCount - rows.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            item {
                BottomSpacer()
            }
        }

        BackButton(onCloseClick)
    }
}

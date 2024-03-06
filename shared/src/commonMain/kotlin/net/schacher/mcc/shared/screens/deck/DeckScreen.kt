package net.schacher.mcc.shared.screens.deck

import IS_ANDROID
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.compose.BackButton
import net.schacher.mcc.shared.design.compose.Card
import net.schacher.mcc.shared.design.compose.CardInfo
import net.schacher.mcc.shared.design.compose.CardRow
import net.schacher.mcc.shared.design.compose.CardRowEntry
import net.schacher.mcc.shared.design.compose.ConfirmationDialog
import net.schacher.mcc.shared.design.compose.FreeBottomSheetContainer
import net.schacher.mcc.shared.design.compose.blurByBottomSheet
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType
import net.schacher.mcc.shared.model.Deck

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeckScreen(
    deck: Deck,
    onDeleteDeckClick: (Int) -> Unit,
    onCloseClick: () -> Unit
) {
    var selectedCard by remember { mutableStateOf<Card?>(null) }
    var deleteDeckShowing by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true,
    )

    if (selectedCard != null) {
        LaunchedEffect(selectedCard) {
            sheetState.show()
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { sheetState.currentValue }.collect {
            if (it == ModalBottomSheetValue.Hidden) {
                selectedCard = null
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        scrimColor = Color.Black.copy(alpha = 0.35f),
        sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
        sheetBackgroundColor = MaterialTheme.colors.surface,
        sheetContent = {
            selectedCard?.let {
                FreeBottomSheetContainer(modifier = Modifier.fillMaxHeight(0.75f)) {
                    CardInfo(card = it)
                }
            }
        }) {

        Content(
            sheetState = sheetState,
            deck = deck,
            onCloseClick = onCloseClick,
            onDeleteDeckClick = { deleteDeckShowing = true },
            onCardClick = { selectedCard = it })
    }

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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Content(
    sheetState: ModalBottomSheetState,
    deck: Deck,
    onCloseClick: () -> Unit,
    onDeleteDeckClick: (Int) -> Unit,
    onCardClick: (Card) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .blurByBottomSheet(sheetState)
            .background(MaterialTheme.colors.background)
    ) {

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Spacer(Modifier.statusBarsPadding().height(16.dp))
            }
            item {
                Row(modifier = Modifier.padding(16.dp)) {
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
                    modifier = Modifier.padding(16.dp),
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
                    modifier = Modifier.padding(16.dp),
                    cardRowEntry = otherCards
                ) {
                    onCardClick(it)
                }
            }
        }

        BackButton(onCloseClick)

        FloatingActionButton(
            onClick = {
                onDeleteDeckClick(deck.id)
            },
            modifier = Modifier.align(Alignment.BottomEnd).navigationBarsPadding()
                .padding(
                    end = 16.dp,
                    bottom = if (IS_ANDROID) 16.dp else 0.dp
                )
                .size(48.dp),
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



package net.schacher.mcc.shared.screens.deck

import IS_ANDROID
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import net.schacher.mcc.shared.design.compose.CardInfo
import net.schacher.mcc.shared.design.compose.CardRow
import net.schacher.mcc.shared.design.compose.CardRowEntry
import net.schacher.mcc.shared.design.compose.FreeBottomSheetContainer
import net.schacher.mcc.shared.design.compose.blurByBottomSheet
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.localization.localize
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeckScreen(
    deck: Deck, onCloseClick: () -> Unit
) {
    var selectedCard by remember { mutableStateOf<Card?>(null) }

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
        Box(
            modifier = Modifier.fillMaxSize()
                .blurByBottomSheet(sheetState)
                .background(MaterialTheme.colors.background)
        ) {
            val entries = deck.cards.groupBy { it.type }
                .map { CardRowEntry("${it.key?.localize()}", it.value) }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(entries.count() + 1) { index ->
                    if (index == 0) {
                        Spacer(Modifier.statusBarsPadding().height(16.dp))
                    }

                    if (index < entries.count()) {
                        CardRow(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                top = if (index == 0) 0.dp else 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            cardRowEntry = entries[index]
                        ) {
                            selectedCard = it
                        }
                    } else {
                        Spacer(Modifier.height(32.dp))
                    }
                }
            }

            FloatingActionButton(
                onClick = onCloseClick,
                modifier = Modifier.align(Alignment.BottomStart).navigationBarsPadding()
                    .padding(
                        start = 20.dp,
                        bottom = if (IS_ANDROID) 20.dp else 0.dp
                    )
                    .size(48.dp),
                contentColor = MaterialTheme.colors.onPrimary,
                backgroundColor = MaterialTheme.colors.primary,
                shape = DefaultShape
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Close"
                )
            }
        }
    }
}



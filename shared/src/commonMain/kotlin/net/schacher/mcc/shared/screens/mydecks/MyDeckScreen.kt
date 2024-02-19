package net.schacher.mcc.shared.screens.mydecks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.compose.DeckRow
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.screens.mydecks.ListItem.DeckItem
import org.koin.compose.koinInject

@Composable
fun MyDecksScreen(
    myDecksViewModel: MyDecksViewModel = koinInject(),
    onDeckClick: (Deck) -> Unit,
    onAddDeckClick: () -> Unit
) {
    val state by myDecksViewModel.state.collectAsState()
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

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp)
                .nestedScroll(nestedScrollConnection)
        ) {
            items(entries.size) { index ->
                if (index == 0) {
                    Spacer(Modifier.statusBarsPadding().height(16.dp))
                }

                when (val entry = entries[index]) {
                    is DeckItem -> DeckRow(entry.deck) { onDeckClick(entry.deck) }
                }

                Spacer(Modifier.height(16.dp))
            }
        }

        AddDeckButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            expanded = expanded
        ) { onAddDeckClick() }
    }
}

@Composable
fun AddDeckButton(modifier: Modifier, expanded: Boolean, onClick: () -> Unit) {
    var horizontalBias by remember { mutableStateOf(1f) }
    val alignment by animateHorizontalAlignmentAsState(horizontalBias)

    horizontalBias = if (expanded) 0f else 1f

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .padding(24.dp)
                .sizeIn(maxHeight = 48.dp, minWidth = 48.dp),
            contentColor = MaterialTheme.colors.onPrimary,
            backgroundColor = MaterialTheme.colors.primary,
            shape = DefaultShape
        ) {
            Row(
                modifier = Modifier.fillMaxHeight()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Create deck"
                )

                AnimatedVisibility(visible = expanded) {
                    Text(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        text = "Create deck"
                    )
                }
            }
        }
    }
}

@Composable
private fun animateHorizontalAlignmentAsState(targetBiasValue: Float): State<BiasAlignment.Horizontal> {
    val bias by animateFloatAsState(targetBiasValue)
    return derivedStateOf { BiasAlignment.Horizontal(bias) }
}

private sealed interface ListItem {
    data class DeckItem(val deck: Deck) : ListItem
}
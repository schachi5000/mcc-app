package net.schacher.mcc.shared.screens.collection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.schacher.mcc.shared.design.compose.Card
import net.schacher.mcc.shared.design.theme.ButtonSize
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.screens.AppScreen
import net.schacher.mcc.shared.screens.mydecks.animateHorizontalAlignmentAsState
import net.schacher.mcc.shared.screens.navigate
import org.koin.compose.koinInject

@Composable
fun CollectionScreen(
    navController: NavController = koinInject(),
    viewModel: CollectionViewModel = koinInject(),
    topInset: Dp,
    onCardClicked: (Card) -> Unit
) {
    val state by viewModel.state.collectAsState()

    CollectionScreen(
        state = state,
        navController = navController,
        topInset = topInset,
        onCardClicked = onCardClicked,
    )
}

@Composable
fun CollectionScreen(
    state: UiState,
    navController: NavController,
    topInset: Dp,
    onCardClicked: (Card) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
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

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection),
        ) {
            items(count = 3) {
                Spacer(modifier = Modifier.height(topInset))
            }

            items(count = state.cardsInCollection.size) { index ->
                val card = state.cardsInCollection[index]
                Card(
                    modifier = Modifier.wrapContentHeight(),
                    card = card,
                    onClick = { onCardClicked(card) })
            }
        }

        FilterButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            label = "Filter collection",
            icon = {
                Icon(
                    Icons.AutoMirrored.Rounded.List,
                    contentDescription = "Filter collection"
                )
            },
            expanded = expanded,
            onClick = { navController.navigate(AppScreen.Packs) }
        )
    }
}

@Composable
fun FilterButton(
    modifier: Modifier,
    label: String,
    expanded: Boolean,
    icon: @Composable() () -> Unit,
    onClick: () -> Unit
) {
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
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .sizeIn(maxHeight = ButtonSize, minWidth = ButtonSize),
            contentColor = MaterialTheme.colors.onPrimary,
            backgroundColor = MaterialTheme.colors.primary,
            shape = DefaultShape
        ) {
            Row(
                modifier = Modifier.fillMaxHeight()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()

                AnimatedVisibility(visible = expanded) {
                    Text(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        text = label
                    )
                }
            }
        }
    }
}
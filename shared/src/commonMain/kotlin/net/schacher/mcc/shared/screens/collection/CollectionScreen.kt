package net.schacher.mcc.shared.screens.collection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.schacher.mcc.shared.design.compose.Card
import net.schacher.mcc.shared.design.compose.ExpandingButton
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.screens.AppScreen
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
    var labeled by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        var expanded by remember { mutableStateOf(false) }
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    expanded = if (expanded) {
                        available.y > -65
                    } else {
                        available.y > 30
                    }

                    return Offset.Zero
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp)
                .nestedScroll(nestedScrollConnection),
        ) {
            items(count = 3) {
                Spacer(modifier = Modifier.height(topInset))
            }

            items(count = state.cardsInCollection.size) { index ->
                val card = state.cardsInCollection[index]

                Column {
                    Card(
                        card = card,
                        modifier = Modifier.wrapContentHeight()
                    ) {
                        onCardClicked(card)
                    }

                    AnimatedVisibility(
                        visible = expanded,
                        modifier = Modifier.padding(vertical = 8.dp)
                            .sizeIn(maxWidth = 128.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = card.name,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.onBackground,
                            maxLines = 2,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        ExpandingButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp),
            label = "Filter collection",
            icon = {
                Icon(
                    Icons.AutoMirrored.Rounded.List,
                    contentDescription = "Filter collection"
                )
            },
            expanded = expanded,
            onClick = {
                labeled = !labeled
                navController.navigate(AppScreen.Packs)
            }
        )
    }
}
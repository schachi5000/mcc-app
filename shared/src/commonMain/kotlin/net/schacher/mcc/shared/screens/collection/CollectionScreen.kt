package net.schacher.mcc.shared.screens.collection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.design.compose.BackHandler
import net.schacher.mcc.shared.design.compose.BottomSheetContainer
import net.schacher.mcc.shared.design.compose.Card
import net.schacher.mcc.shared.design.compose.ExpandingButton
import net.schacher.mcc.shared.design.compose.FilterFlowRow
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.screens.AppScreen
import net.schacher.mcc.shared.screens.navigate
import net.schacher.mcc.shared.screens.search.Filter
import net.schacher.mcc.shared.utils.replace
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
        onApplyFilerClick = viewModel::onApplyFilterClicked
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CollectionScreen(
    state: UiState,
    navController: NavController,
    topInset: Dp,
    onCardClicked: (Card) -> Unit,
    onApplyFilerClick: (Set<Filter>) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    BackHandler(sheetState.isVisible) {
        scope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        scrimColor = Color.Black.copy(alpha = 0.35f),
        sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
        sheetBackgroundColor = MaterialTheme.colors.surface,
        sheetContent = {
            BottomSheetContainer {
                FilterContent(
                    state = state,
                    onBackClick = { scope.launch { sheetState.hide() } },
                    onSelectPacksClicked = { navController.navigate(AppScreen.Packs) },
                    onApplyFilerClick = { filters ->
                        onApplyFilerClick(filters)
                        scope.launch { sheetState.hide() }
                    }
                )
            }
        }
    ) {
        var labeled by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            var expanded by remember { mutableStateOf(false) }
            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
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
                    scope.launch {
                        if (sheetState.isVisible) {
                            sheetState.hide()
                        } else {
                            sheetState.show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun FilterContent(
    state: UiState,
    onBackClick: () -> Unit = {},
    onApplyFilerClick: (Set<Filter>) -> Unit = {},
    onSelectPacksClicked: () -> Unit = {},
) {
    var selectedFilters by remember { mutableStateOf(state.filters.toList()) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "Filter",
            color = MaterialTheme.colors.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        FilterFlowRow(
            modifier = Modifier.padding(vertical = 16.dp),
            filters = selectedFilters.toSet(),
            onFilterClicked = { filter ->
                selectedFilters = selectedFilters.replace(
                    filter, filter.copy(active = !filter.active)
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .height(124.dp)
                .padding(16.dp)
                .clickable { onSelectPacksClicked() },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("My packs")
            Icon(
                Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = "Select packs"
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = onBackClick,
                shape = DefaultShape,
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = MaterialTheme.colors.surface
                )
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colors.primary,
                    text = "Cancel"
                )
            }

            Spacer(Modifier.width(16.dp))

            TextButton(
                modifier = Modifier.weight(1f),
                onClick = { onApplyFilerClick(selectedFilters.toSet()) },
                shape = DefaultShape,
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colors.onPrimary,
                    text = "Apply"
                )
            }
        }
    }
}
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import net.schacher.mcc.shared.design.compose.BottomSpacer
import net.schacher.mcc.shared.design.compose.Card
import net.schacher.mcc.shared.design.compose.ExpandingButton
import net.schacher.mcc.shared.design.compose.FilterFlowRow
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.screens.AppRoute
import net.schacher.mcc.shared.screens.navigate
import net.schacher.mcc.shared.screens.search.Filter
import net.schacher.mcc.shared.screens.search.Filter.Type.OWNED
import net.schacher.mcc.shared.utils.replace
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel = koinViewModel(),
    navController: NavController = koinInject(),
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
        scrimColor = Color.Black.copy(alpha = 0.75f),
        sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetContent = {
            BottomSheetContainer(
                background = MaterialTheme.colors.surface
            ) {
                FilterContent(
                    state = state,
                    onSelectPacksClicked = { navController.navigate(AppRoute.Packs) },
                    onApplyFilerClick = { onApplyFilerClick(it) }
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                    .padding(horizontal = ContentPadding)
                    .nestedScroll(nestedScrollConnection),
            ) {
                items(count = 3) {
                    Spacer(modifier = Modifier.statusBarsPadding().height(topInset))
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
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                items(count = 3) {
                    BottomSpacer()
                }
            }

            ExpandingButton(
                label = "Filter Collection",
                icon = {
                    Icon(
                        Icons.AutoMirrored.Rounded.List,
                        contentDescription = "Filter Collection"
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
    onApplyFilerClick: (Set<Filter>) -> Unit = {},
    onSelectPacksClicked: () -> Unit = {},
) {
    var selectedFilters by remember { mutableStateOf(state.filters.toList()) }
    var showOnlyOwned by remember {
        mutableStateOf(
            state.filters.find { it.type == OWNED }?.active ?: false
        )
    }

    LaunchedEffect(showOnlyOwned) {
        val ownedFilter = selectedFilters.find { it.type == OWNED }
        val finalFilters =
            selectedFilters.replace(ownedFilter, Filter(OWNED, showOnlyOwned))

        onApplyFilerClick(finalFilters.toSet())
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = "Filter & Appearance",
            color = MaterialTheme.colors.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        FilterFlowRow(
            modifier = Modifier.fillMaxWidth()
                .padding(
                    start = ContentPadding,
                    end = ContentPadding
                ),
            filters = selectedFilters.toMutableSet().also {
                it.removeAll { it.type == OWNED }
            },
            onFilterClicked = { filter ->
                selectedFilters = selectedFilters.replace(
                    filter, filter.copy(active = !filter.active)
                )

                onApplyFilerClick(selectedFilters.toSet())
            }
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
                .height(64.dp)
                .clickable { showOnlyOwned = !showOnlyOwned }
                .padding(horizontal = ContentPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show only owned")
            Switch(
                checked = showOnlyOwned,
                onCheckedChange = null
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .height(64.dp)
                .clickable { onSelectPacksClicked() }
                .padding(horizontal = ContentPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("My packs")
            Icon(
                Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = "Select packs"
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}
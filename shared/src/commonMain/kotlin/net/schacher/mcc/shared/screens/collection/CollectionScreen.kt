package net.schacher.mcc.shared.screens.collection

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.collection
import net.schacher.mcc.shared.design.compose.BackHandler
import net.schacher.mcc.shared.design.compose.BottomSpacer
import net.schacher.mcc.shared.design.compose.ExpandingButton
import net.schacher.mcc.shared.design.compose.FilterFlowRow
import net.schacher.mcc.shared.design.compose.LabeledCard
import net.schacher.mcc.shared.design.compose.Header
import net.schacher.mcc.shared.design.compose.SecondaryButton
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.screens.AppRoute
import net.schacher.mcc.shared.screens.main.BottomSheetDelegate
import net.schacher.mcc.shared.screens.navigate
import net.schacher.mcc.shared.screens.search.Filter
import net.schacher.mcc.shared.screens.search.Filter.Type.OWNED
import net.schacher.mcc.shared.utils.replace
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel = koinViewModel(),
    navController: NavController = koinInject(),
    topInset: Dp = ContentPadding,
    bottomSheetDelegate: BottomSheetDelegate,
    onCardClicked: (Card) -> Unit
) {
    val state by viewModel.state.collectAsState()

    CollectionScreen(
        state = state,
        navController = navController,
        topInset = topInset,
        onCardClicked = onCardClicked,
        bottomSheetDelegate = bottomSheetDelegate,
        onApplyFilerClick = viewModel::onApplyFilterClicked
    )
}

@Composable
fun CollectionScreen(
    state: UiState,
    navController: NavController,
    topInset: Dp,
    bottomSheetDelegate: BottomSheetDelegate,
    onCardClicked: (Card) -> Unit,
    onApplyFilerClick: (Set<Filter>) -> Unit
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
            header {
                Row(modifier = Modifier.statusBarsPadding().padding(top = topInset)) {
                    Header(stringResource(Res.string.collection))
                }
            }

            items(count = state.cardsInCollection.size) { index ->
                val card = state.cardsInCollection[index]
                Column {
                    LabeledCard(
                        card = card,
                        label = card.name,
                        showLabel = expanded,
                        modifier = Modifier.wrapContentHeight()
                    ) {
                        onCardClicked(card)
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
                bottomSheetDelegate.show {
                    FilterContent(
                        state = state,
                        onSelectPacksClicked = {
                            bottomSheetDelegate.hide()
                            navController.navigate(AppRoute.Packs)
                        },
                        onApplyFilerClick = { onApplyFilerClick(it) }
                    )
                }
            }
        )
    }
}

@Composable
fun FilterContent(
    state: UiState,
    onApplyFilerClick: (Set<Filter>) -> Unit = {},
    onSelectPacksClicked: () -> Unit = {},
) {
    var selectedFilters by remember { mutableStateOf(state.filters.toList()) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = "Filter & Appearance",
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h6,
        )

        FilterFlowRow(
            modifier = Modifier.fillMaxWidth()
                .padding(
                    start = ContentPadding,
                    end = ContentPadding
                ),
            filters = selectedFilters.toMutableSet(),
            onFilterClicked = { filter ->
                selectedFilters = selectedFilters.replace(
                    filter, filter.copy(active = !filter.active)
                )

                onApplyFilerClick(selectedFilters.toSet())
            }
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable { onSelectPacksClicked() }
                .padding(horizontal = ContentPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecondaryButton(
                modifier = Modifier.fillMaxWidth(),
                label = "Select Packs",
                onClick = onSelectPacksClicked
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

private fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}
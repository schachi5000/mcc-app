package net.schacher.mcc.shared.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.compose.CardRow
import net.schacher.mcc.shared.design.compose.CardRowEntry
import net.schacher.mcc.shared.design.compose.isKeyboardVisible
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.design.theme.color
import net.schacher.mcc.shared.design.theme.isContrastRatioSufficient
import net.schacher.mcc.shared.localization.label
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.screens.search.Filter.Type
import net.schacher.mcc.shared.screens.search.Filter.Type.AGGRESSION
import net.schacher.mcc.shared.screens.search.Filter.Type.BASIC
import net.schacher.mcc.shared.screens.search.Filter.Type.JUSTICE
import net.schacher.mcc.shared.screens.search.Filter.Type.LEADERSHIP
import net.schacher.mcc.shared.screens.search.Filter.Type.OWNED
import net.schacher.mcc.shared.screens.search.Filter.Type.PROTECTION
import net.schacher.mcc.shared.utils.defaultSort
import org.koin.compose.koinInject


@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = koinInject(),
    topInset: Dp,
    onCardClicked: (Card) -> Unit
) {
    val state by searchViewModel.state.collectAsState()

    SearchScreen(
        state = state,
        onCardClicked = onCardClicked,
        topInset = topInset,
        onSearch = searchViewModel::onSearch,
        onFilterClicked = searchViewModel::onFilterClicked
    )
}


@Composable
fun SearchScreen(
    state: UiState,
    topInset: Dp,
    onCardClicked: (Card) -> Unit,
    onSearch: (String?) -> Unit,
    onFilterClicked: (Filter) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize().imePadding()) {
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {

                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    focusManager.clearFocus()
                    return Offset.Zero
                }
            }
        }

        val entries = state.result
            .groupBy { it.type }
            .mapNotNull { (type, cards) ->
                type?.let {
                    CardRowEntry(it.label, cards.defaultSort())
                }
            }
            .sortedBy { it.title }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .nestedScroll(nestedScrollConnection)
        ) {
            items(entries.count()) { item ->
                if (item == 0) {
                    Spacer(Modifier.statusBarsPadding().height(topInset))
                }

                CardRow(
                    modifier = Modifier.padding(
                        start = ContentPadding,
                        top = if (item == 0) 0.dp else 16.dp,
                        end = ContentPadding,
                        bottom = 16.dp
                    ),
                    cardRowEntry = entries[item]
                ) {
                    focusManager.clearFocus()
                    onCardClicked(it)
                }
            }
        }

        Column(
            modifier = Modifier
                .statusBarsPadding()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            FilterRow(
                modifier = Modifier.padding(bottom = 4.dp),
                filters = state.filters,
                horizontalPadding = ContentPadding
            ) {
                onFilterClicked(it)
            }

            SearchBar(
                horizontalPadding = ContentPadding,
                onDoneClick = { focusManager.clearFocus() }) { query ->
                onSearch(query)
            }
        }
    }
}

@Composable
fun SearchBar(
    horizontalPadding: Dp = 16.dp,
    onDoneClick: () -> Unit,
    onQueryChange: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Row(modifier = Modifier.fillMaxWidth().height(48.dp)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .weight(1f)
                .padding(
                    start = horizontalPadding,
                    end = if (input.isNotEmpty()) 0.dp else horizontalPadding
                ),
            shape = DefaultShape,
            color = MaterialTheme.colors.surface,
            border = BorderStroke(
                if (isKeyboardVisible()) 2.dp else 1.dp,
                if (isKeyboardVisible()) MaterialTheme.colors.primary else MaterialTheme.colors.background
            )
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = input,
                textStyle = MaterialTheme.typography.body1,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = MaterialTheme.colors.onSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { onDoneClick() }
                ),
                singleLine = true,
                onValueChange = {
                    input = it
                    onQueryChange(it)
                },
                label = null,
            )
        }

        AnimatedVisibility(visible = input.isNotEmpty()) {
            IconButton(
                modifier = Modifier
                    .padding(start = 8.dp, end = horizontalPadding)
                    .size(48.dp)
                    .background(MaterialTheme.colors.primary, DefaultShape),
                onClick = {
                    input = ""
                    onQueryChange("")
                }) {
                Icon(
                    Icons.Rounded.Clear, "Clear",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

@Composable
fun FilterRow(
    modifier: Modifier = Modifier,
    filters: Set<Filter>,
    horizontalPadding: Dp,
    onFilterClicked: (Filter) -> Unit = {}
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEachIndexed { index, filter ->
            item {
                SearchFilterChip(
                    modifier = Modifier.padding(
                        start = if (index == 0) horizontalPadding else 0.dp,
                        end = if (index == filters.count() - 1) horizontalPadding else 0.dp
                    ),
                    color = when (filter.type) {
                        AGGRESSION -> Aspect.AGGRESSION.color
                        PROTECTION -> Aspect.PROTECTION.color
                        JUSTICE -> Aspect.JUSTICE.color
                        LEADERSHIP -> Aspect.LEADERSHIP.color
                        BASIC,
                        OWNED -> MaterialTheme.colors.primary
                    },
                    label = filter.type.label,
                    selected = filter.active
                ) {
                    onFilterClicked(filter)
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchFilterChip(
    modifier: Modifier = Modifier,
    label: String,
    color: Color,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    FilterChip(
        modifier = modifier,
        onClick = onClick,
        selected = selected,
        shape = DefaultShape,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colors.background
        ),
        colors = ChipDefaults.filterChipColors(
            backgroundColor = MaterialTheme.colors.surface,
            selectedContentColor = if (color.isContrastRatioSufficient(Color.White)) {
                Color.White
            } else {
                Color.Black
            },
            selectedBackgroundColor = color
        )
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold
        )
    }
}

val Type.label: String
    @Composable
    get() = when (this) {
        OWNED -> "In Besitz"
        BASIC -> "Basis"
        AGGRESSION -> Aspect.AGGRESSION.label
        PROTECTION -> Aspect.PROTECTION.label
        JUSTICE -> Aspect.JUSTICE.label
        LEADERSHIP -> Aspect.LEADERSHIP.label
    }
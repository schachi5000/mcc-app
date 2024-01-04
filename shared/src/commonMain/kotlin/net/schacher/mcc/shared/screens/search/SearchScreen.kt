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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.compose.Entry
import net.schacher.mcc.shared.design.compose.EntryRow
import net.schacher.mcc.shared.design.compose.isKeyboardVisible
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.design.theme.color
import net.schacher.mcc.shared.design.theme.isContrastRatioSufficient
import net.schacher.mcc.shared.localization.localize
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
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject


@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = koinInject(),
    onCardClicked: (Card) -> Unit
) {
    val state by searchViewModel.state.collectAsState()

    SearchScreen(
        state = state,
        onCardClicked = onCardClicked,
        onSearch = searchViewModel::onSearch,
        onFilterClicked = searchViewModel::onFilterClicked
    )
}


@Composable
fun SearchScreen(
    state: UiState,
    onCardClicked: (Card) -> Unit,
    onSearch: (String?) -> Unit,
    onFilterClicked: (Filter) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {

                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    focusManager.clearFocus()
                    return Offset.Zero
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .nestedScroll(nestedScrollConnection)
        ) {
            val entries = createEntries(state.result)
            items(entries.count()) { item ->
                if (item == 0) {
                    Spacer(Modifier.statusBarsPadding().padding(bottom = 148.dp))
                }

                EntryRow(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = if (item == 0) 0.dp else 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    entry = entries[item]
                ) {
                    focusManager.clearFocus()
                    onCardClicked(it)
                }
            }
        }

        Column(
            modifier = Modifier.background(shade)
                .statusBarsPadding()
                .padding(vertical = 16.dp)
        ) {
            SearchBar(onDoneClick = { focusManager.clearFocus() }) { query ->
                onSearch(query)
            }

            FilterRow(
                modifier = Modifier.padding(vertical = 8.dp),
                filters = state.filters
            ) {
                onFilterClicked(it)
            }
        }
    }
}

private val shade: Brush
    @Composable
    get() = Brush.verticalGradient(
        colorStops = arrayOf(
            0f to MaterialTheme.colors.background.copy(alpha = 0.8f),
            0.8f to MaterialTheme.colors.background.copy(alpha = 0.7f),
            1f to MaterialTheme.colors.background.copy(alpha = 0.0f)
        )
    )

private fun createEntries(cards: List<Card>): List<Entry> =
    cards.groupBy { it.type }.mapNotNull { (type, cards) ->
        type?.let {
            Entry(it.localize(), cards.defaultSort())
        }
    }.sortedBy { it.title }

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SearchBar(
    onDoneClick: () -> Unit,
    onQueryChange: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Row(modifier = Modifier.fillMaxWidth().height(48.dp)) {
        AnimatedVisibility(visible = isKeyboardVisible()) {
            IconButton(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Transparent, DefaultShape),
                onClick = {
                    focusRequester.freeFocus()
                    onDoneClick()
                }
            ) {
                Icon(
                    painterResource("ic_arrow_back.xml"), "Clear",
                    tint = MaterialTheme.colors.onBackground
                )
            }
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(
                    start = if (isKeyboardVisible()) 0.dp else 16.dp,
                    end = if (input.isNotEmpty()) 0.dp else 16.dp
                ),
            shape = DefaultShape,
            color = MaterialTheme.colors.surface,
            border = BorderStroke(
                2.dp,
                MaterialTheme.colors.primary
            ).takeIf { isKeyboardVisible() },
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = input,
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

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        AnimatedVisibility(visible = input.isNotEmpty()) {
            IconButton(
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .size(48.dp)
                    .background(MaterialTheme.colors.surface, DefaultShape),
                onClick = {
                    input = ""
                    onQueryChange("")
                }) {
                Icon(
                    Icons.Rounded.Clear, "Clear",
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@Composable
fun FilterRow(
    modifier: Modifier = Modifier,
    filters: Set<Filter>,
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
                        start = if (index == 0) 16.dp else 0.dp,
                        end = if (index == filters.count() - 1) 16.dp else 0.dp
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
        Text(label)
    }
}


private val Type.label: String
    get() = when (this) {
        OWNED -> "In Besitz"
        BASIC -> "Basis"
        AGGRESSION -> Aspect.AGGRESSION.localize()
        PROTECTION -> Aspect.PROTECTION.localize()
        JUSTICE -> Aspect.JUSTICE.localize()
        LEADERSHIP -> Aspect.LEADERSHIP.localize()
    }
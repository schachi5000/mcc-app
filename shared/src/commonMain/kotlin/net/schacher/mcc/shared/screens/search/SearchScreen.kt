package net.schacher.mcc.shared.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
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
import net.schacher.mcc.shared.model.Card
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
        onSearch = searchViewModel::onSearch
    )
}

@Composable
fun SearchScreen(
    state: UiState,
    onCardClicked: (Card) -> Unit,
    onSearch: (String?) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        val entries = state.result
            .groupBy { it.type }
            .map { Entry("${it.key} (${it.value.size})", it.value) }

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
            items(entries.count()) { item ->
                if (item == 0) {
                    Spacer(Modifier.statusBarsPadding().padding(bottom = 88.dp))
                }

                EntryRow(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = if (item == 0) 0.dp else 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ), entry = entries[item]
                ) {
                    onCardClicked(it)
                }
            }
        }

        Row(modifier = Modifier.padding(vertical = 16.dp)) {
            SearchBar(onDoneClick = { focusManager.clearFocus() }) { query ->
                onSearch(query)
            }
        }
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun SearchBar(
    onDoneClick: () -> Unit,
    onQueryChange: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Row(modifier = Modifier.fillMaxWidth().height(56.dp)) {
        AnimatedVisibility(visible = isKeyboardVisible()) {
            IconButton(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Transparent),
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
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colors.surface,
            border = BorderStroke(2.dp, MaterialTheme.colors.primary).takeIf { isKeyboardVisible() },
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
                    .size(56.dp)
                    .background(MaterialTheme.colors.surface, CircleShape),
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

package net.schacher.mcc.shared.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.compose.Entry
import net.schacher.mcc.shared.design.compose.EntryRow
import net.schacher.mcc.shared.model.Card
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    onCardClicked: (Card) -> Unit
) {
    val state by searchViewModel.state.collectAsState()
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

        Row(modifier = Modifier.padding(16.dp)) {
            SearchBar(onDoneClick = { focusManager.clearFocus() }) { query ->
                searchViewModel.onSearch(query)
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

    Row {
        AnimatedVisibility(visible = isKeyboardVisible()) {
            IconButton(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(48.dp)
                    .background(MaterialTheme.colors.surface, CircleShape),
                onClick = { onDoneClick() }
            ) {
                Icon(
                    painterResource("ic_hide_keyboard.xml"), "Clear",
                    tint = MaterialTheme.colors.onBackground
                )
            }
        }

        val focusRequester = remember { FocusRequester() }

        TextField(
            modifier = Modifier
                .weight(1f)
                .size(48.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface, RoundedCornerShape(32.dp))
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

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        AnimatedVisibility(visible = input.isNotEmpty()) {
            IconButton(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(48.dp)
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

@Composable
fun isKeyboardVisible(): Boolean = WindowInsets.ime.getBottom(LocalDensity.current) > 0
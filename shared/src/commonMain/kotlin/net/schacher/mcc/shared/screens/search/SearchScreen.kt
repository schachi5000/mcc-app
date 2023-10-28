package net.schacher.mcc.shared.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.Entry
import net.schacher.mcc.shared.design.EntryRow
import net.schacher.mcc.shared.model.Card

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    snackbarHostState: SnackbarHostState,
    onCardClicked: (Card) -> Unit
) {
    val state by searchViewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        val entries = state.result
            .groupBy { it.type }
            .map { Entry("${it.key} (${it.value.size})", it.value) }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
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
            SearchBar { query ->
                searchViewModel.onSearch(query)
            }
        }
    }
}

@Composable
private fun SearchBar(onQueryChanged: (String) -> Unit) {
    var input by remember { mutableStateOf("") }

    TextField(
        modifier = Modifier.fillMaxWidth()
            .background(MaterialTheme.colors.surface, RoundedCornerShape(32.dp)),
        value = input,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            cursorColor = MaterialTheme.colors.onSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            IconButton(enabled = input.isNotEmpty(),
                onClick = {
                    input = ""
                    onQueryChanged("")
                }) {
                Icon(
                    Icons.Rounded.Clear,
                    "Trailing icon",
                    tint = MaterialTheme.colors.onSurface.copy(
                        alpha = if (input.isEmpty()) 0.5f else 1f
                    )
                )
            }
        },
        maxLines = 1,
        onValueChange = {
            input = it
            onQueryChanged(it)
        },
        label = null,
    )
}
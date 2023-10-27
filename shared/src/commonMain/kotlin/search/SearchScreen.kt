package search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
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
import model.Card
import screens.InspectScreen

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    onCardClicked: (Card) -> Unit
) {
    val state by searchViewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Row(modifier = Modifier.padding(16.dp)) {
            SearchBar { query ->
                searchViewModel.onSearch(query)
            }
        }

        InspectScreen(cards = state.result, onCardClicked = onCardClicked)
    }
}

@Composable
private fun SearchBar(onQueryChanged: (String) -> Unit) {
    var input by remember { mutableStateOf("") }

    TextField(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        value = input,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color(0xFFF0F0F0),
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            Icon(Icons.Rounded.Clear, "", tint = Color.Black)
        },
        onValueChange = {
            input = it
            onQueryChanged(it)
        },
        label = null,
    )
}
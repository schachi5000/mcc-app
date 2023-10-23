package search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.query ?: "",
                onValueChange = { searchViewModel.onSearch(it) },
                label = { Text("Label") }
            )
        }

        InspectScreen(cards = state.result, onCardClicked = onCardClicked)
    }
}
package net.schacher.mcc.shared.screens.featured

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.GameDeck

@Composable
fun FeaturedScreen(featuredViewModel: FeaturedViewModel) {
    val state by featuredViewModel.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        LazyColumn {
            items(state.decks.size) { index ->
                GameDeck(state.decks[index])
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
package net.schacher.mcc.design.preview.screens

import androidx.compose.runtime.Composable
import net.schacher.mcc.design.ThemedPreviews
import net.schacher.mcc.shared.design.theme.MccTheme
import net.schacher.mcc.shared.screens.search.SearchScreen
import net.schacher.mcc.shared.screens.search.UiState

@ThemedPreviews
@Composable
fun SearchScreenPreview() {
    MccTheme {
        SearchScreen(
            state = UiState(true, emptyList()),
            onCardClicked = {},
            onSearch = {}
        )
    }
}
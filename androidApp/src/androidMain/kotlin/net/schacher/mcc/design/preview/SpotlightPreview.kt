package net.schacher.mcc.design.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import net.schacher.mcc.design.ThemedPreviews
import net.schacher.mcc.shared.design.theme.MccTheme
import net.schacher.mcc.shared.screens.spotlight.SpotlightScreen
import net.schacher.mcc.shared.screens.spotlight.SpotlightViewModel

@ThemedPreviews
@Composable
fun SpotlightScreenPreview() {
    MccTheme {
        SpotlightScreen(
            state = SpotlightViewModel.UiState(emptyMap(), true),
            topInset = 0.dp,
            onDeckClick = {},
            onRefresh = {}
        )
    }
}
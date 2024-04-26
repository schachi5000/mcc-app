package net.schacher.mcc.design.preview

import androidx.compose.runtime.Composable
import net.schacher.mcc.design.ThemedPreviews
import net.schacher.mcc.shared.design.theme.MccTheme
import net.schacher.mcc.shared.screens.spotlight.SpotlightScreen
import net.schacher.mcc.shared.screens.spotlight.SpotlightViewModel

@ThemedPreviews
@Composable
fun SpotlightScreenPreview() {
    MccTheme {
        SpotlightScreen(
            SpotlightViewModel.UiState(emptyMap(), true)
        ) {}
    }
}
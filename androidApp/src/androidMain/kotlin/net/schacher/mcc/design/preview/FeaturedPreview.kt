package net.schacher.mcc.design.preview

import androidx.compose.runtime.Composable
import net.schacher.mcc.design.ThemedPreviews
import net.schacher.mcc.shared.design.theme.MccTheme
import net.schacher.mcc.shared.screens.featured.FeaturedScreen
import net.schacher.mcc.shared.screens.featured.FeaturedUiState

@ThemedPreviews
@Composable
fun FeaturedScreenPreview() {
    MccTheme {
        FeaturedScreen(
            FeaturedUiState(emptyMap(), true)
        ) {}
    }
}
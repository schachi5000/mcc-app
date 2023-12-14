package net.schacher.mcc.design

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.compose.BottomSheetContainer
import net.schacher.mcc.shared.design.compose.OptionsEntry
import net.schacher.mcc.shared.design.compose.OptionsGroup
import net.schacher.mcc.shared.design.compose.ShimmerBox
import net.schacher.mcc.shared.design.theme.DeckShape
import net.schacher.mcc.shared.design.theme.MccTheme
import net.schacher.mcc.shared.screens.search.SearchBar

@ThemedPreviews
@Composable
fun SearchBarPreview() {
    MccTheme {
        SearchBar(onDoneClick = {}, onQueryChange = {})
    }
}

@ThemedPreviews
@Composable
fun OptionsGroupPreview() {
    MccTheme {
        OptionsGroup("Title") {
            OptionsEntry(label = "Entry", icon = { }) {
            }
        }
    }
}

@ThemedPreviews
@Composable
fun DefaultBottomSheetPreview() {
    MccTheme {
        BottomSheetContainer {}
    }
}

@ThemedPreviews
@Composable
fun ShimmerBoxPreview() {
    MccTheme {
        ShimmerBox(
            Modifier
                .size(48.dp)
                .clip(DeckShape),
        )
    }
}
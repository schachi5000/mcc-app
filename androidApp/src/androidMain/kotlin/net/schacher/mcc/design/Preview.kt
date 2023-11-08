package net.schacher.mcc.design

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.compose.BottomSheetContainer
import net.schacher.mcc.shared.design.compose.OptionsEntry
import net.schacher.mcc.shared.design.compose.OptionsGroup
import net.schacher.mcc.shared.design.compose.ShimmerBox
import net.schacher.mcc.shared.design.theme.DeckShape
import net.schacher.mcc.shared.screens.search.SearchBar

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar(onDoneClick = {}, onQueryChange = {})
}

@Preview
@Composable
fun OptionsGroupPreview() {
    OptionsGroup("Title") {
        OptionsEntry(label = "Entry", icon = { }) {
        }
    }
}

@Preview
@Composable
fun DefaultBottomSheetPreview() {
    BottomSheetContainer {

    }
}

@Preview
@Composable
fun ShimmerBoxPreview() {
    ShimmerBox(
        Modifier
            .size(48.dp)
            .clip(DeckShape),
        Color.Red
    )
}
package net.schacher.mcc.design

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.schacher.mcc.shared.design.compose.BottomSheetContainer
import net.schacher.mcc.shared.design.compose.OptionsEntry
import net.schacher.mcc.shared.design.compose.OptionsGroup
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
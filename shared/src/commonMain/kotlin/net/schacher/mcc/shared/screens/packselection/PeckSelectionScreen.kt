package net.schacher.mcc.shared.screens.packselection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.model.Pack
import org.koin.compose.koinInject

@Composable
fun PackSelectionScreen(
    viewModel: PackSelectionViewModel = koinInject(),
    onBackPress: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colors.background)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            text = "Your Packs",
            style = MaterialTheme.typography.h2,
            color = MaterialTheme.colors.onBackground
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.packs.forEach {
                item {
                    SelectionEntry(it.pack, it.selected) {
                        viewModel.onPackClicked(it)
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionEntry(pack: Pack, selected: Boolean, onClick: (String) -> Unit) {
    if (selected) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onClick(pack.code) },
            shape = DefaultShape,
        ) {
            Text(pack.name)
        }
    } else {
        OutlinedButton(
            onClick = { onClick(pack.code) },
            modifier = Modifier.fillMaxWidth(),
            shape = DefaultShape,
            colors = ButtonDefaults.outlinedButtonColors()
        ) {
            Text(
                text = pack.name,
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}
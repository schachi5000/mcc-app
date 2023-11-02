package net.schacher.mcc.shared.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.design.OptionsEntry
import net.schacher.mcc.shared.design.OptionsGroup
import net.schacher.mcc.shared.repositories.CardRepository

@Composable
fun SettingsScreen(cardRepository: CardRepository, snackbarHostState: SnackbarHostState) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
    ) {
        OptionsGroup("Datenbank") {
            OptionsEntry(label = "Sync with MarvelCDB",
                imageVector = Icons.Rounded.Refresh,
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Datebank aktualisieren")
                        cardRepository.refresh()
                    }
                })

            OptionsEntry(
                label = "Alle Einträge löschen",
                imageVector = Icons.Rounded.Delete,
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Datebank wird gelöscht")
                        cardRepository.deleteAllCards()
                    }
                })
        }
    }
}
package net.schacher.mcc.shared.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.database
import marvelchampionscompanion.shared.generated.resources.more
import net.schacher.mcc.shared.design.compose.ConfirmationDialog
import net.schacher.mcc.shared.design.compose.Header
import net.schacher.mcc.shared.design.compose.OptionsEntry
import net.schacher.mcc.shared.design.compose.OptionsGridEntry
import net.schacher.mcc.shared.design.compose.OptionsGroup
import net.schacher.mcc.shared.design.compose.maxSpanItem
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.screens.settings.SettingsViewModel.UiState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val GRID_COUNT = 2

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = koinViewModel(),
    topInset: Dp = ContentPadding,
    onPackSelectionClick: () -> Unit,
    onLogoutClicked: () -> Unit
) {
    val state by settingsViewModel.state.collectAsState()

    SettingsScreen(
        state = state,
        topInset = topInset,
        onPackSelectionClick = onPackSelectionClick,
        onLogoutClick = onLogoutClicked,
        onSyncClick = { settingsViewModel.onSyncClick() },
        onWipeDatabaseClick = { settingsViewModel.onWipeDatabaseClick() },
    )
}

@Composable
fun SettingsScreen(
    state: UiState,
    topInset: Dp,
    onPackSelectionClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSyncClick: () -> Unit,
    onWipeDatabaseClick: () -> Unit
) {
    var deleteDatabaseDialog by remember { mutableStateOf(false) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COUNT),
        modifier = Modifier.fillMaxWidth().padding(horizontal = ContentPadding),
        horizontalArrangement = Arrangement.spacedBy(ContentPadding),
        verticalArrangement = Arrangement.spacedBy(ContentPadding),
    ) {
        maxSpanItem {
            Row(Modifier.statusBarsPadding().padding(top = topInset)) {
                Spacer(Modifier.height(ContentPadding))
                Header(stringResource(Res.string.more))
                Spacer(Modifier.height(ContentPadding))
            }
        }

        item {
            OptionsGridEntry("Cards in your collection", state.cardsInCollection.toString())
        }

        item {
            OptionsGridEntry("Cards in local database", state.cardCount.toString())
        }

        item {
            OptionsGridEntry("My decks", state.userDeckCount.toString())
        }

        item {
            OptionsGridEntry(
                "Owned packs",
                "${state.packsInCollectionCount} of ${state.packCount}",
            ) { onPackSelectionClick() }
        }

        maxSpanItem {
            Column {
                OptionsGroup(stringResource(Res.string.database)) {
                    OptionsEntry(
                        label = "Sync with MarvelCDB",
                        imageVector = Icons.Rounded.Refresh,
                        onClick = onSyncClick
                    )

                    OptionsEntry(
                        label = "Alle Einträge löschen",
                        imageVector = Icons.Rounded.Delete,
                        onClick = { deleteDatabaseDialog = true })
                }

                TextButton(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    onClick = { onLogoutClick() },
                    shape = DefaultShape,
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = MaterialTheme.colors.surface
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colors.primary,
                        text = if (state.guestLogin) "Login" else "Logout"
                    )
                }

                Spacer(Modifier.size(16.dp))

                Text(
                    modifier = Modifier.padding(16.dp).fillMaxWidth().alpha(0.5f),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onBackground,
                    text = state.versionName
                )
            }
        }
    }

    if (deleteDatabaseDialog) {
        ConfirmationDialog(title = "Datenbank löschen",
            message = "Möchtest du wirklich alle Einträge löschen?",
            onDismiss = { deleteDatabaseDialog = false },
            onConfirm = {
                onWipeDatabaseClick()
                deleteDatabaseDialog = false
            })
    }
}
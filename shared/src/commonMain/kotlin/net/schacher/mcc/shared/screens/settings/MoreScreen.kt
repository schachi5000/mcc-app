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
import marvelchampionscompanion.shared.generated.resources.cards_in_collection
import marvelchampionscompanion.shared.generated.resources.cards_in_database
import marvelchampionscompanion.shared.generated.resources.database
import marvelchampionscompanion.shared.generated.resources.delete_database_dialog_message
import marvelchampionscompanion.shared.generated.resources.delete_database_dialog_title
import marvelchampionscompanion.shared.generated.resources.delete_local_data
import marvelchampionscompanion.shared.generated.resources.login
import marvelchampionscompanion.shared.generated.resources.logout
import marvelchampionscompanion.shared.generated.resources.more
import marvelchampionscompanion.shared.generated.resources.my_decks
import marvelchampionscompanion.shared.generated.resources.of
import marvelchampionscompanion.shared.generated.resources.owned_packs
import marvelchampionscompanion.shared.generated.resources.sync_with_marvelcdb
import net.schacher.mcc.shared.design.compose.ConfirmationDialog
import net.schacher.mcc.shared.design.compose.Header
import net.schacher.mcc.shared.design.compose.OptionsEntry
import net.schacher.mcc.shared.design.compose.OptionsGridEntry
import net.schacher.mcc.shared.design.compose.OptionsGroup
import net.schacher.mcc.shared.design.compose.maxSpanItem
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.screens.settings.MoreViewModel.UiState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val GRID_CELL_SIZE = 2

@Composable
fun MoreScreen(
    settingsViewModel: MoreViewModel = koinViewModel(),
    topInset: Dp = ContentPadding,
    onPackSelectionClick: () -> Unit,
    onLogoutClicked: () -> Unit
) {
    val state by settingsViewModel.state.collectAsState()

    MoreScreen(
        state = state,
        topInset = topInset,
        onPackSelectionClick = onPackSelectionClick,
        onLogoutClick = onLogoutClicked,
        onSyncClick = { settingsViewModel.onSyncClick() },
        onWipeDatabaseClick = { settingsViewModel.onWipeDatabaseClick() },
    )
}

@Composable
fun MoreScreen(
    state: UiState,
    topInset: Dp,
    onPackSelectionClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSyncClick: () -> Unit,
    onWipeDatabaseClick: () -> Unit
) {
    var deleteDatabaseDialog by remember { mutableStateOf(false) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_CELL_SIZE),
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
            OptionsGridEntry(
                stringResource(Res.string.my_decks),
                state.userDeckCount.toString()
            )
        }

        item {
            OptionsGridEntry(
                stringResource(Res.string.owned_packs),
                "${state.packsInCollectionCount}",
            ) { onPackSelectionClick() }
        }

        item {
            OptionsGridEntry(
                stringResource(Res.string.cards_in_collection),
                state.cardsInCollection.toString()
            )
        }

        item {
            OptionsGridEntry(
                stringResource(Res.string.cards_in_database),
                state.cardCount.toString()
            )
        }

        maxSpanItem {
            Column {
                OptionsGroup(stringResource(Res.string.database)) {
                    OptionsEntry(
                        label = stringResource(Res.string.sync_with_marvelcdb),
                        imageVector = Icons.Rounded.Refresh,
                        onClick = onSyncClick
                    )

                    OptionsEntry(
                        label = stringResource(Res.string.delete_local_data),
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
                        text = if (state.guestLogin) {
                            stringResource(Res.string.login)
                        } else {
                            stringResource(Res.string.logout)
                        }
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
        ConfirmationDialog(title = stringResource(Res.string.delete_database_dialog_title),
            message = stringResource(Res.string.delete_database_dialog_message),
            onDismiss = { deleteDatabaseDialog = false },
            onConfirm = {
                onWipeDatabaseClick()
                deleteDatabaseDialog = false
            })
    }
}
package net.schacher.mcc.shared.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.database
import marvelchampionscompanion.shared.generated.resources.ic_cards
import marvelchampionscompanion.shared.generated.resources.ic_deck
import net.schacher.mcc.shared.design.compose.ConfirmationDialog
import net.schacher.mcc.shared.design.compose.OptionsEntry
import net.schacher.mcc.shared.design.compose.OptionsGroup
import net.schacher.mcc.shared.design.theme.DefaultShape
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@ExperimentalResourceApi
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = koinInject(),
    onPackSelectionClick: () -> Unit,
    onLogoutClicked: () -> Unit
) {
    val state by settingsViewModel.state.collectAsState()
    var deleteDatabaseDialog by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = keyframes {
            durationMillis = 1000
        })
    )

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        OptionsGroup(stringResource(Res.string.database)) {
            OptionsEntry(
                label = "Sync with MarvelCDB",
                imageVector = Icons.Rounded.Refresh,
                onClick = {
                    settingsViewModel.onSyncClick()
                })

            OptionsEntry(
                label = "Alle Einträge löschen",
                imageVector = Icons.Rounded.Delete,
                onClick = { deleteDatabaseDialog = true })
        }

        Spacer(Modifier.size(16.dp))

        OptionsGroup("Einträge") {
            AnimatedVisibility(state.syncInProgress) {
                OptionsEntry(label = "Aktualisieren Datenbank", icon = {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "",
                        modifier = Modifier.padding(end = 16.dp).rotate(angle).size(18.dp)
                    )
                })
            }

            OptionsEntry(
                label = "${state.cardCount} Karten", iconResource = Res.drawable.ic_cards
            )

            OptionsEntry(
                label = "${state.deckCount} Decks", iconResource = Res.drawable.ic_deck
            )

            OptionsEntry(label = "${state.packsInCollectionCount} of ${state.packCount} Packs",
                iconResource = Res.drawable.ic_deck,
                onClick = { onPackSelectionClick() })
        }

        TextButton(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            onClick = { onLogoutClicked() },
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

    if (deleteDatabaseDialog) {
        ConfirmationDialog(title = "Datenbank löschen",
            message = "Möchtest du wirklich alle Einträge löschen?",
            onDismiss = { deleteDatabaseDialog = false },
            onConfirm = {
                settingsViewModel.onWipeDatabaseClick()
                deleteDatabaseDialog = false
            })
    }
}
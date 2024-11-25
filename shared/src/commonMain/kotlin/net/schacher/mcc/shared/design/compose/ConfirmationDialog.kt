package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.cancel
import marvelchampionscompanion.shared.generated.resources.ok
import net.schacher.mcc.shared.design.theme.DefaultShape
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onDismiss: (() -> Unit)? = null,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss ?: {},
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = DefaultShape,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                )
                Text(
                    text = message,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                )

                Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                    if (onDismiss != null) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = DefaultShape
                        ) {
                            Text(
                                text = stringResource(Res.string.cancel),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentSize(Alignment.Center),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.button,
                            )
                        }

                        Spacer(Modifier.width(8.dp))
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = DefaultShape
                    ) {
                        Text(
                            text = stringResource(Res.string.ok),
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.button,
                        )
                    }
                }
            }
        }
    }
}
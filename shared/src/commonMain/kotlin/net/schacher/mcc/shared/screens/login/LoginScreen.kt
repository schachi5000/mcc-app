package net.schacher.mcc.shared.screens.login

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.login_as_guest
import marvelchampionscompanion.shared.generated.resources.login_info_message
import marvelchampionscompanion.shared.generated.resources.login_info_title
import marvelchampionscompanion.shared.generated.resources.login_with_marvelcdb
import marvelchampionscompanion.shared.generated.resources.splash_screen
import net.schacher.mcc.shared.design.compose.ConfirmationDialog
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.design.theme.HorizontalScreenPadding
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    onLogInClicked: () -> Unit,
    onContinueAsGuestClicked: () -> Unit,
) {
    var confirmationSeen by remember { mutableStateOf(false) }
    var showingConfirmation by remember { mutableStateOf(false) }

    var blur by remember { mutableStateOf(0.dp) }
    val animatedBlur by animateDpAsState(
        targetValue = blur,
        animationSpec = tween()
    )
    blur = if (showingConfirmation) 5.dp else 0.dp

    Box(
        modifier = Modifier.fillMaxSize()
            .blur(animatedBlur)

    ) {
        Image(
            painter = painterResource(Res.drawable.splash_screen),
            contentDescription = "Splash Screen",
            contentScale = ContentScale.Crop,
            modifier = Modifier.blur(12.dp)
        )

        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to MaterialTheme.colors.surface.copy(alpha = 0.1f),
                        0.4f to MaterialTheme.colors.surface.copy(alpha = 0.8f),
                        0.6f to MaterialTheme.colors.surface.copy(alpha = 1f),
                        1f to MaterialTheme.colors.surface.copy(alpha = 1f)
                    )
                )
            )
        )

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    vertical = 96.dp,
                    horizontal = HorizontalScreenPadding,
                ),
        ) {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (confirmationSeen) {
                        onLogInClicked()
                    } else {
                        showingConfirmation = true
                    }
                },
                shape = DefaultShape,
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colors.onPrimary,
                    text = stringResource(Res.string.login_with_marvelcdb)
                )
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onContinueAsGuestClicked,
                shape = DefaultShape,
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = MaterialTheme.colors.background
                )
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colors.primary,
                    text = stringResource(Res.string.login_as_guest)
                )
            }
        }
    }

    if (showingConfirmation) {
        confirmationSeen = true
        ConfirmationDialog(
            title = stringResource(Res.string.login_info_title),
            message = stringResource(Res.string.login_info_message),
            onConfirm = {
                showingConfirmation = false
                onLogInClicked.invoke()
            }
        )
    }
}


package net.schacher.mcc.shared.screens.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import net.schacher.mcc.shared.screens.login.LoginScreen
import net.schacher.mcc.shared.screens.main.MainScreen
import org.koin.compose.koinInject

private const val LOG_IN_MILLIS = 450

private const val LOG_OUT_MILLIS = 450

@Composable
fun AppScreen(
    appViewModel: AppViewModel = koinInject(),
    onLogInClicked: () -> Unit = {}
) {
    val loggedIn = appViewModel.state.collectAsState()

    AnimatedContent(targetState = loggedIn.value, transitionSpec = {
        if (targetState) {
            slideInVertically(
                tween(LOG_IN_MILLIS),
                initialOffsetY = { fillHeight -> fillHeight }) togetherWith
                    slideOutVertically(tween(
                        LOG_IN_MILLIS
                    ), targetOffsetY = { fillHeight -> -fillHeight })
        } else {
            slideInVertically(
                tween(LOG_OUT_MILLIS),
                initialOffsetY = { fillHeight -> -fillHeight }) togetherWith
                    slideOutVertically(
                        tween(LOG_OUT_MILLIS),
                        targetOffsetY = { fillHeight -> fillHeight })
        }
    }) {
        if (it) {
            MainScreen()
        } else {
            LoginScreen(onLogInClicked = onLogInClicked) {
                appViewModel.onGuestLoginClicked()
            }
        }
    }
}
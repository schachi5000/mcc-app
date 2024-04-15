package net.schacher.mcc.shared.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import kotlinx.coroutines.launch
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.splash_screen
import net.schacher.mcc.shared.auth.AuthHandler
import net.schacher.mcc.shared.auth.PersistingAuthHandler
import net.schacher.mcc.shared.design.compose.BackHandler
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.utils.debug
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import pro.schacher.mcc.BuildConfig

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LoginScreen(
    onGuestLogin: () -> Unit,
) {
    var loginBottomSheetShowing by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.splash_screen),
            contentDescription = "Splash Screen",
            contentScale = ContentScale.Crop,
            modifier = Modifier.blur(6.dp)
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
                .padding(horizontal = 32.dp, vertical = 96.dp),
        ) {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { loginBottomSheetShowing = true },
                shape = DefaultShape,
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colors.onPrimary,
                    text = "Login with MarvelCDB"
                )
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onGuestLogin() },
                shape = DefaultShape,
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = MaterialTheme.colors.background
                )
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colors.primary,
                    text = "Continue as Guest"
                )
            }
        }
    }

    if (loginBottomSheetShowing) {
        ModalBottomLoginSheet(
            onDismiss = {
                Logger.debug { "Dismiss bottom sheet" }
                loginBottomSheetShowing = false
            })
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalBottomLoginSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    var webViewShowing by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true,
    )

    BackHandler(webViewShowing || sheetState.isVisible) {
        onDismiss()
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        scrimColor = Color.Black.copy(alpha = 0.35f),
        sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
        sheetBackgroundColor = MaterialTheme.colors.surface,
        sheetContent = {
            LoginWebView(
                modifier = modifier.heightIn(min = 300.dp, max = 600.dp).imePadding(),
                onAccessGranted = { },
                onAccessDenied = {
                    webViewShowing = false
                    onDismiss()
                })
        }) {
        Box(modifier = modifier.fillMaxSize().background(Color.Transparent))
    }

    val scope = rememberCoroutineScope()
    scope.launch {
        if (webViewShowing) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.isVisible }.collect { isVisible ->
            if (!isVisible && webViewShowing) {
                onDismiss()
            }
            webViewShowing = isVisible
        }
    }

    LaunchedEffect(Unit) {
        webViewShowing = true
    }
}

@Composable
private fun LoginWebView(
    modifier: Modifier = Modifier,
    authHandler: AuthHandler = koinInject(),
    onAccessGranted: () -> Unit,
    onAccessDenied: () -> Unit
) {
    val webViewState = rememberWebViewState(BuildConfig.OAUTH_URL)
    val navigator = rememberWebViewNavigator()

    val lastLoadedUrl = webViewState.lastLoadedUrl
    if (lastLoadedUrl != null && lastLoadedUrl.startsWith(PersistingAuthHandler.APP_SCHEME)) {
        if (authHandler.handleCallbackUrl(lastLoadedUrl)) {
            onAccessGranted()
        } else {
            onAccessDenied()
        }
    }

    Column(modifier.background(Color.White)) {
        TopAppBar(
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.primary,
            title = { Text(text = "Login") },
            navigationIcon = {
                IconButton(onClick = {
                    if (navigator.canGoBack) {
                        navigator.navigateBack()
                    } else {
                        onAccessDenied()
                    }
                }) {
                    Icon(
                        imageVector = if (navigator.canGoBack) {
                            Icons.AutoMirrored.Filled.ArrowBack
                        } else {
                            Icons.Filled.Close
                        },
                        contentDescription = "Back",
                        tint = MaterialTheme.colors.primary
                    )
                }
            })

        val loading = webViewState.loadingState is LoadingState.Loading
        val progress = (webViewState.loadingState as? LoadingState.Loading)?.progress ?: 0f

        AnimatedVisibility(loading) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )
        }

        WebView(
            state = webViewState,
            modifier = Modifier.navigationBarsPadding(),
            navigator = navigator
        )
    }
}
package net.schacher.mcc.shared.screens.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import net.schacher.mcc.shared.design.compose.Animation
import net.schacher.mcc.shared.screens.AppScreen
import net.schacher.mcc.shared.screens.card.CardScreen
import net.schacher.mcc.shared.screens.deck.DeckScreen
import net.schacher.mcc.shared.screens.login.LoginScreen
import net.schacher.mcc.shared.screens.main.MainScreen
import org.koin.compose.koinInject

private const val LOG_IN_MILLIS = 450

private const val LOG_OUT_MILLIS = 450

@Composable
fun AppScreen(
    appViewModel: AppViewModel = koinInject(),
    navController: NavHostController = rememberNavController(),
    onLogInClicked: () -> Unit
) {
    val loggedIn = appViewModel.state.collectAsState()

    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()

    // Get the name of the current screen
//    val currentScreen = AppScreen.valueOf(
//        backStackEntry?.destination?.name ?: AppScreen.Login.name
//    )

    NavHost(
        navController = navController,
        startDestination = AppScreen.Login.route,
        modifier = Modifier.fillMaxSize(),
        popExitTransition = { Animation.fullscreenExit },
        popEnterTransition = { Animation.fullscreenEnter },
        exitTransition = { Animation.fullscreenExit },
        enterTransition = { Animation.fullscreenEnter }
    ) {
        composable(AppScreen.Login.route) {
            LoginScreen(onLogInClicked = onLogInClicked, onContinueAsGuestClicked = {
                navController.navigate(AppScreen.Main.route)
            })
        }
        composable(AppScreen.Main.route) {
            MainScreen(navController = navController)
        }

        composable(
            route = AppScreen.Deck.route,
            arguments = listOf(navArgument("deckId") {
                type = NavType.IntType
            })
        ) {
            it.arguments?.getInt("deckId")?.let { deckId ->
                DeckScreen(
                    deckId = deckId,
                    navController = navController,
                    onDeleteDeckClick = {})
            }
        }
        composable(
            route = AppScreen.Card.route,
            arguments = listOf(navArgument("cardCode") {
                type = NavType.StringType
            })
        ) {
            it.arguments?.getString("cardCode")?.let { cardCode ->
                CardScreen(cardCode = cardCode) {
                    navController.popBackStack()
                }
            }
        }
    }

    if (loggedIn.value) {
        navController.navigate(AppScreen.Main.route)
    } else {
        navController.popBackStack(AppScreen.Login.route, false)
    }

//    LoginScreen(
//        onLogInClicked = onLogInClicked,
//        onContinueAsGuestClicked = { appViewModel.onGuestLoginClicked() }
//    )

//    AnimatedContent(
//        targetState = loggedIn.value,
//        transitionSpec = {
//            if (targetState) {
//                slideInVertically(
//                    tween(LOG_IN_MILLIS),
//                    initialOffsetY = { fillHeight -> fillHeight }) togetherWith
//                        slideOutVertically(tween(
//                            LOG_IN_MILLIS
//                        ), targetOffsetY = { fillHeight -> -fillHeight })
//            } else {
//                slideInVertically(
//                    tween(LOG_OUT_MILLIS),
//                    initialOffsetY = { fillHeight -> -fillHeight }) togetherWith
//                        slideOutVertically(
//                            tween(LOG_OUT_MILLIS),
//                            targetOffsetY = { fillHeight -> fillHeight })
//            }
//        }) {
//
//        if (it) {
//            MainScreen()
//        } else {
//            LoginScreen(
//                onLogInClicked = onLogInClicked,
//                onContinueAsGuestClicked = { appViewModel.onGuestLoginClicked() }
//            )
//        }
//    }
}
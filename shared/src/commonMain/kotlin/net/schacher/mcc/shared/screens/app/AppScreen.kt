package net.schacher.mcc.shared.screens.app

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import net.schacher.mcc.shared.design.compose.Animation
import net.schacher.mcc.shared.screens.AppScreen
import net.schacher.mcc.shared.screens.card.CardScreen
import net.schacher.mcc.shared.screens.deck.DeckScreen
import net.schacher.mcc.shared.screens.login.LoginScreen
import net.schacher.mcc.shared.screens.main.MainScreen
import net.schacher.mcc.shared.screens.newdeck.NewDeckScreen
import net.schacher.mcc.shared.screens.packselection.PackSelectionScreen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppScreen(
    appViewModel: AppViewModel = koinViewModel(),
    navController: NavController = koinInject(),
    onLogInClicked: () -> Unit
) {
    NavHost(
        navController = navController as NavHostController,
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        startDestination = AppScreen.Login.route,
        popEnterTransition = { fadeIn() + slideInHorizontally { _ -> -100 } },
        popExitTransition = { Animation.fullscreenExit },
        enterTransition = { Animation.fullscreenEnter },
        exitTransition = { fadeOut() + slideOutHorizontally { _ -> -100 } },
    ) {
        composable(AppScreen.Login.route) {
            LoginScreen(
                onLogInClicked = onLogInClicked,
                onContinueAsGuestClicked = { appViewModel.onGuestLoginClicked() })
        }

        composable(AppScreen.Main.route) {
            MainScreen()
        }

        composable(AppScreen.AddDeck.route) {
            NewDeckScreen(
                onBackPress = { navController.popBackStack() },
                onNewDeckSelected = { _, _ -> },
            )
        }

        composable(AppScreen.Packs.route) {
            PackSelectionScreen()
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
                    onDeleteDeckClick = {}
                )
            }
        }
        composable(
            route = AppScreen.Card.route,
            arguments = listOf(navArgument("cardCode") {
                type = NavType.StringType
            })
        ) {
            it.arguments?.getString("cardCode")?.let { cardCode ->
                CardScreen(cardCode = cardCode)
            }
        }
    }

    val loggedIn = appViewModel.state.collectAsState()
    if (loggedIn.value) {
        navController.navigate(AppScreen.Main.route)
    } else {
        navController.popBackStack(AppScreen.Login.route, false)
    }
}
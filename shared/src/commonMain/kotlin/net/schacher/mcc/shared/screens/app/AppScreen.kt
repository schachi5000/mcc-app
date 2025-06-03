package net.schacher.mcc.shared.screens.app

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.schacher.mcc.shared.design.compose.Animation
import net.schacher.mcc.shared.design.compose.BackHandler
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.screens.AppRoute
import net.schacher.mcc.shared.screens.card.CardScreen
import net.schacher.mcc.shared.screens.deck.DeckScreen
import net.schacher.mcc.shared.screens.login.LoginScreen
import net.schacher.mcc.shared.screens.main.MainScreen
import net.schacher.mcc.shared.screens.mydecks.MyDecksScreen
import net.schacher.mcc.shared.screens.newdeck.NewDeckScreen
import net.schacher.mcc.shared.screens.packselection.PackSelectionScreen
import net.schacher.mcc.shared.screens.setResultAndPopBackstack
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppScreen(
    appViewModel: AppViewModel = koinViewModel(),
    navController: NavController = koinInject(),
    onLogInClicked: () -> Unit,
    onQuitApp: () -> Unit
) {
    NavHost(
        navController = navController as NavHostController,
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        startDestination = AppRoute.Login.route,
        popEnterTransition = { fadeIn() + slideInHorizontally { _ -> -100 } },
        popExitTransition = { Animation.fullscreenExit },
        enterTransition = { Animation.fullscreenEnter },
        exitTransition = { fadeOut() + slideOutHorizontally { _ -> -100 } },
    ) {
        composable(AppRoute.Login.route) {
            LoginScreen(
                onLogInClicked = onLogInClicked,
                onContinueAsGuestClicked = { appViewModel.onGuestLoginClicked() })
        }

        composable(AppRoute.Main.route) {
            MainScreen()
        }

        composable(AppRoute.AddDeck.route) {
            NewDeckScreen(
                onBackPress = { navController.popBackStack() },
                onNewDeckCreated = { navController.popBackStack() }
            )
        }

        composable(AppRoute.Packs.route) {
            PackSelectionScreen()
        }

        composable(
            route = AppRoute.Deck.route,
            arguments = AppRoute.Deck.navArguments
        ) {
            it.arguments?.getInt(AppRoute.Deck.navArguments[0].name)?.let { deckId ->
                DeckScreen(deckId = deckId)
            }
        }
        composable(
            route = AppRoute.Card.route,
            arguments = AppRoute.Card.navArguments
        ) {
            it.arguments?.getString(AppRoute.Card.navArguments[0].name)?.let { cardCode ->
                CardScreen(cardCode = cardCode)
            }
        }

        composable(AppRoute.SelectDeck.route) {
            MyDecksScreen(
                topInset = ContentPadding,
                onDeckClick = { navController.setResultAndPopBackstack(it.id) }
            )
        }
    }

    val currentBackStack by navController.currentBackStack.collectAsState()
    BackHandler(currentBackStack.last().destination.route == AppRoute.Main.route) {
        onQuitApp()
    }

    val loggedIn = appViewModel.state.collectAsState()
    LaunchedEffect(loggedIn.value) {
        if (loggedIn.value) {
            navController.navigate(AppRoute.Main.route)
        } else {
            navController.popBackStack(AppRoute.Login.route, false)
        }
    }
}
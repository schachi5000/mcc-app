package net.schacher.mcc.shared.screens

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.StateFlow

enum class AppRoute(
    val route: String,
    var navArguments: List<NamedNavArgument> = emptyList()
) {
    Login(route = "login"),
    Main(route = "main"),
    Deck(
        route = "deck/{deckId}",
        navArguments = listOf(navArgument("deckId") {
            type = NavType.IntType
        })
    ),
    SelectDeck(route = "select_deck"),
    Card(
        route = "card/{cardCode}",
        listOf(navArgument("cardCode") {
            type = NavType.StringType
        })
    ),
    AddDeck(route = "add_deck"),
    Packs(route = "packs")
}

fun NavController.navigate(appRoute: AppRoute) {
    this.navigate(appRoute.route)
}

private const val RESULT_KEY = "result"

fun NavController.setResultAndPopBackstack(result: String) {
    this.previousBackStackEntry?.savedStateHandle?.set(RESULT_KEY, result)
    this.popBackStack()
}

fun NavController.setResultAndPopBackstack(result: Number) {
    this.previousBackStackEntry?.savedStateHandle?.set(RESULT_KEY, result)
    this.popBackStack()
}

fun <T> NavController.resultState(): StateFlow<T?>? =
    this.previousBackStackEntry?.savedStateHandle?.getStateFlow<T?>(RESULT_KEY, null)

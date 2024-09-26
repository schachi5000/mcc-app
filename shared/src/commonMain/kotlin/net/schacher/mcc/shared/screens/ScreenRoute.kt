package net.schacher.mcc.shared.screens

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument

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
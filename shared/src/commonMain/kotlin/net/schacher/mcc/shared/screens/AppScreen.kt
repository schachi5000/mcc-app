package net.schacher.mcc.shared.screens

import androidx.navigation.NavController

enum class AppScreen(val route: String) {
    Login(route = "login"),
    Main(route = "main"),
    Deck(route = "deck/{deckId}"),
    Card(route = "card/{cardCode}"),
    AddDeck(route = "add_deck"),
    Packs(route = "packs")
}

fun NavController.navigate(appScreen: AppScreen) {
    this.navigate(appScreen.route)
}
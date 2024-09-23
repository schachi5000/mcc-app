package net.schacher.mcc.shared.screens

enum class AppScreen(val route: String) {
    Login(route = "login"),
    Main(route = "main"),
    Deck(route = "deck/{deckId}"),
    Card(route = "card/{cardCode}"),
    AddDeck(route = "add_deck")
}
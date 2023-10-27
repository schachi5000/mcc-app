package net.schacher.mcc.shared.model

data class Deck(
    val id: String,
    val name: String,
    val cards: List<Card>
)
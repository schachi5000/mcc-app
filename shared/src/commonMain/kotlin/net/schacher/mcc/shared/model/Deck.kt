package net.schacher.mcc.shared.model


data class Deck(
    val id: Int,
    val name: String,
    val heroCard: Card,
    val aspect: Aspect?,
    val cards: List<Card>
) {
    val validDeck: Boolean
        get() = cards.size == 40
}

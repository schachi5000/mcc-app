package net.schacher.mcc.shared.model

private const val VALID_DECK_SIZE = 40

data class Deck(
    val id: Int,
    val name: String,
    val hero: Card,
    val aspect: Aspect?,
    val cards: List<Card>
) {
    val validDeck: Boolean
        get() = cards.size == VALID_DECK_SIZE

    val requiredDecks = cards.map { it.packCode }.toSet()
}

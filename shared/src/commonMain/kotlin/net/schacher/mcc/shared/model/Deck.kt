package net.schacher.mcc.shared.model

import net.schacher.mcc.shared.model.CardType.HERO

data class Deck(
    val id: String,
    val name: String,
    val cards: List<Card>
) {
    val heroCard: Card? = cards.firstOrNull { it.type == HERO }

    val validDeck: Boolean
        get() = cards.size == 40 && heroCard != null
}
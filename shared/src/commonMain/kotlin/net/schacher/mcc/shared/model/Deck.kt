package net.schacher.mcc.shared.model

data class Deck(
    val id: Int,
    val name: String,
    val hero: Card,
    val aspect: Aspect?,
    val cards: List<Card>,
    val cardsCodes: List<String>,
    val description: String? = null,
    val version: String?,
    val problem: String? = null
) {
    val requiredPacks = this.cards.map { it.packCode }.toSet()
}

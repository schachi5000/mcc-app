package net.schacher.mcc.shared.model


data class Card(
    val code: String,
    val position: Int,
    val type: String?,
    val name: String,
    val faction: Faction,
    val packCode: String,
    val cost: Int? = null,
    val aspect: Aspect? = null,
    val linkedCard: Card? = null,
    val imagePath: String? = null
) {
    val orientation = when (type) {
        CardType.SIDE_SCHEME,
        CardType.MAIN_SCHEME -> CardOrientation.LANDSCAPE

        else -> CardOrientation.PORTRAIT
    }
}

enum class CardOrientation {
    PORTRAIT, LANDSCAPE
}

object CardType {
    const val HERO = "hero"
    const val ALLY = "ally"
    const val EVENT = "event"
    const val SUPPORT = "support"
    const val UPGRADE = "upgrade"
    const val RESOURCE = "resource"
    const val VILLAIN = "villain"
    const val MAIN_SCHEME = "main_scheme"
    const val SIDE_SCHEME = "side_scheme"
    const val ATTACHMENT = "attachment"
    const val MINION = "minion"
    const val TREACHERY = "treachery"
    const val ENVIRONMENT = "environment"
    const val OBLIGATION = "obligation"
}

enum class Faction{
    AGGRESSION,
    JUSTICE,
    ENCOUNTER,
    HERO,
    BASIC,
    LEADERSHIP,
    PROTECTION,
    CAMPAIGN,
    POOL,
}

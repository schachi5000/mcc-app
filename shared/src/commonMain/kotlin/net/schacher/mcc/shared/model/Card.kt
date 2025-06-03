package net.schacher.mcc.shared.model

import net.schacher.mcc.shared.model.CardOrientation.LANDSCAPE
import net.schacher.mcc.shared.model.CardOrientation.PORTRAIT
import net.schacher.mcc.shared.model.CardType.MAIN_SCHEME
import net.schacher.mcc.shared.model.CardType.PLAYER_SIDE_SCHEME
import net.schacher.mcc.shared.model.CardType.SIDE_SCHEME


data class Card(
    val code: String,
    val position: Int,
    val type: CardType?,
    val name: String,
    val faction: Faction,
    val setCode: String? = null,
    val setName: String? = null,
    val packCode: String,
    val packName: String,
    val cost: Int? = null,
    val text: String?,
    val boostText: String?,
    val attackText: String?,
    val quote: String?,
    val traits: String?,
    val aspect: Aspect? = null,
    val linkedCard: Card? = null,
    val linkedCardCode: String? = null,
    val imagePath: String? = null,
    val primaryColor: String? = null,
    val secondaryColor: String? = null,
) {
    val orientation = when (type) {
        SIDE_SCHEME,
        MAIN_SCHEME,
        PLAYER_SIDE_SCHEME -> LANDSCAPE

        else -> if (ForcedLandscapes.codes.contains(this.code)) {
            LANDSCAPE
        } else {
            PORTRAIT
        }
    }
}

private object ForcedLandscapes {
    val codes = listOf("42001c")
}

enum class CardOrientation {
    PORTRAIT, LANDSCAPE
}

enum class CardType {
    HERO,
    ALTER_EGO,
    ALLY,
    EVENT,
    SUPPORT,
    UPGRADE,
    RESOURCE,
    VILLAIN,
    MAIN_SCHEME,
    SIDE_SCHEME,
    PLAYER_SIDE_SCHEME,
    ATTACHMENT,
    MINION,
    TREACHERY,
    ENVIRONMENT,
    OBLIGATION,
}

enum class Faction {
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

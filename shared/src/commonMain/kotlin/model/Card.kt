package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val code: String,
    val position: Int,
    @SerialName("type_code")
    val type: String,
    val name: String,
    @SerialName("linked_card")
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
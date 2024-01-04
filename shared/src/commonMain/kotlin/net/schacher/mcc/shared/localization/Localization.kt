package net.schacher.mcc.shared.localization

import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.CardType

// This is just a premature solution to have the localization in one place.
// Once MOKO-Resources works as intended, this should be removed
object Localization {
    const val MY_DECKS = "Meine Decks"
    const val SPOTLIGHT = "Spotlight"
    const val SEARCH = "Suche"
    const val MORE = "Mehr"
}

fun CardType.localize(): String {
    return when (this) {
        CardType.HERO -> "Held"
        CardType.ALLY -> "Verbündeter"
        CardType.EVENT -> "Ereignis"
        CardType.SUPPORT -> "Unterstützung"
        CardType.UPGRADE -> "Upgrade"
        CardType.RESOURCE -> "Ressource"
        CardType.VILLAIN -> "Schurke"
        CardType.MAIN_SCHEME -> "Hauptplan"
        CardType.SIDE_SCHEME -> "Nebenplan"
        CardType.PLAYER_SIDE_SCHEME -> "Spielernebenplan"
        CardType.ATTACHMENT -> "Anhang"
        CardType.MINION -> "Scherge"
        CardType.TREACHERY -> "Verrat"
        CardType.ENVIRONMENT -> "Umgebung"
        CardType.OBLIGATION -> "Verpflichtung"
    }
}

fun Aspect.localize(): String {
    return when (this) {
        Aspect.AGGRESSION -> "Aggression"
        Aspect.PROTECTION -> "Schutz"
        Aspect.JUSTICE -> "Gerechtigkeit"
        Aspect.LEADERSHIP -> "Führung"
    }
}
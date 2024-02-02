package net.schacher.mcc.shared.localization

import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.CardType

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
package net.schacher.mcc.shared.model

enum class Aspect {
    AGGRESSION,
    PROTECTION,
    JUSTICE,
    LEADERSHIP;

    override fun toString(): String = when (this) {
        AGGRESSION -> "Aggression"
        PROTECTION -> "Schutz"
        JUSTICE -> "Gerechtigkeit"
        LEADERSHIP -> "Führung"
    }
}
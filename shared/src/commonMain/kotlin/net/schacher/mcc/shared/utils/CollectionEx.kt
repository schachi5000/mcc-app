package net.schacher.mcc.shared.utils

import net.schacher.mcc.shared.model.Card

fun Collection<Card>.defaultSort(): List<Card> {
    val byAspect = this.groupBy { it.aspect ?: it.type }.values

    return byAspect.map {
        val byCost = it.groupBy { it.cost ?: -1 }.values.sortedBy { it.first().cost ?: -1 }
        val byName = byCost.map { it.sortedBy { it.name } }

        byName.flatten()
    }.flatten()
}

fun Collection<Card>.distinctByName(): List<Card> {
    return this.distinctBy { "${it.name} ${it.aspect}" }
}
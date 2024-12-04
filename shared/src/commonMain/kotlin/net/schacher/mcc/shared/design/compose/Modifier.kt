package net.schacher.mcc.shared.design.compose

import androidx.compose.ui.Modifier

fun Modifier.applyIf(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier =
    if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
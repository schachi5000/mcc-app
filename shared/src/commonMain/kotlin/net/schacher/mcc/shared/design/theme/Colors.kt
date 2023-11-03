package net.schacher.mcc.shared.design.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import net.schacher.mcc.shared.design.theme.AspectColors.Aggression
import net.schacher.mcc.shared.design.theme.AspectColors.Justice
import net.schacher.mcc.shared.design.theme.AspectColors.Leadership
import net.schacher.mcc.shared.design.theme.AspectColors.Protection
import net.schacher.mcc.shared.model.Aspect

val LightColorScheme = lightColors(
    background = Color(0xffededed),
    surface = Color(0xFFFFFFFF),
    primary = Color(0xFF145da1),
)

val DarkColorScheme = darkColors(
    background = Color(0xFF000005),
    surface = Color(0xFF242424),
    primary = Color(0xFF145da1),
)

object AspectColors {
    val Aggression = Color(0xFFd32f2f)
    val Protection = Color(0xFF388e3c)
    val Justice = Color(0xFFfbc02d)
    val Leadership = Color(0xFF1976d2)
}

fun Aspect.getColor(): Color = when (this) {
    Aspect.AGGRESSION -> Aggression
    Aspect.PROTECTION -> Protection
    Aspect.JUSTICE -> Justice
    Aspect.LEADERSHIP -> Leadership
}
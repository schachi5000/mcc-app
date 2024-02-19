package net.schacher.mcc.shared.design.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import net.schacher.mcc.shared.design.theme.AspectColors.Aggression
import net.schacher.mcc.shared.design.theme.AspectColors.Justice
import net.schacher.mcc.shared.design.theme.AspectColors.Leadership
import net.schacher.mcc.shared.design.theme.AspectColors.Protection
import net.schacher.mcc.shared.model.Aspect
import kotlin.math.max
import kotlin.math.min

val LightColorScheme = lightColors(
    background = Color(0xffededed),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000),
    primary = Color(0xFF706fd3),
    onPrimary = Color(0xFFFFFFFF),
)

val DarkColorScheme = darkColors(
    background = Color(0xFF000005),
    onBackground = Color(0xFFf4f4f4),
    surface = Color(0xFF242424),
    onSurface = Color(0xFFf0f0f0),
    primary = Color(0xFF706fd3),
    onPrimary = Color(0xFFFFFFFF)
)

const val MIN_CONTRAST_RATIO = 4.0f

object SleeveColors {
    val Blue = Color(0xFF185ea4)
    val Yellow = Color(0xFFe98a02)
    val Black = Color(0xFF000000)
}

object AspectColors {
    val Aggression = Color(0xFFd32f2f)
    val Protection = Color(0xFF388e3c)
    val Justice = Color(0xFFe5cf28)
    val Leadership = Color(0xFF1976d2)
}

val Aspect?.color: Color
    get() = when (this) {
        Aspect.AGGRESSION -> Aggression
        Aspect.PROTECTION -> Protection
        Aspect.JUSTICE -> Justice
        Aspect.LEADERSHIP -> Leadership
        else -> Color(0xffc7c7c7)
    }

fun Color.getContrastRation(foreground: Color): Float {
    val foregroundLuminance = foreground.luminance() + 0.05f
    val backgroundLuminance = this.luminance() + 0.05f

    return max(foregroundLuminance, backgroundLuminance) /
            min(foregroundLuminance, backgroundLuminance)
}

fun Color.isContrastRatioSufficient(foreground: Color): Boolean {
    return getContrastRation(foreground) >= MIN_CONTRAST_RATIO
}
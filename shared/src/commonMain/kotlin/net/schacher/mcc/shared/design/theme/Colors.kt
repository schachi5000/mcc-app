package net.schacher.mcc.shared.design.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import net.schacher.mcc.shared.design.theme.AspectColors.Aggression
import net.schacher.mcc.shared.design.theme.AspectColors.Justice
import net.schacher.mcc.shared.design.theme.AspectColors.Leadership
import net.schacher.mcc.shared.design.theme.AspectColors.Protection
import net.schacher.mcc.shared.model.Aspect
import kotlin.math.max
import kotlin.math.min

private val primary = Color(0xFF0074df)

val LightColorScheme = lightColors(
    primary = primary,
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    surface = Color(0xfff0f0f0),
    onSurface = Color(0xFF000000),
)

val DarkColorScheme = darkColors(
    primary = primary,
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xff000000),
    onBackground = Color(0xFFf9f9f9),
    surface = Color(0xff14171f),
    onSurface = Color(0xFFf0f0f0),
)

const val MIN_CONTRAST_RATIO = 3.5

object BottomSheetColors {
    val Scrim = Color(0xff000000).copy(alpha = 0.5f)

    val Background: Color
        @Composable
        get() = if (MaterialTheme.colors.isLight) {
            LightColorScheme.background
        } else {
            DarkColorScheme.background
        }
}

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

fun Color.Companion.parseColor(colorString: String): Color {
    // Remove the '#' if it exists
    val cleanedHex = colorString.removePrefix("#")

    // Parse the hex string
    val colorInt = cleanedHex.toLong(16)

    // Extract components
    return when (cleanedHex.length) {
        6 -> {
            // If no alpha is provided, assume it's fully opaque
            val r = (colorInt shr 16 and 0xFF).toFloat() / 255
            val g = (colorInt shr 8 and 0xFF).toFloat() / 255
            val b = (colorInt and 0xFF).toFloat() / 255
            Color(r, g, b, 1f)
        }

        8 -> {
            // If alpha is included
            val a = (colorInt shr 24 and 0xFF).toFloat() / 255
            val r = (colorInt shr 16 and 0xFF).toFloat() / 255
            val g = (colorInt shr 8 and 0xFF).toFloat() / 255
            val b = (colorInt and 0xFF).toFloat() / 255
            Color(r, g, b, a)
        }

        else -> throw IllegalArgumentException("Invalid hex color: $colorString")
    }
}
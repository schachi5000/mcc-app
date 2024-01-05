package net.schacher.mcc.shared.design

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

/**
 * Composition local that is used to determine if the current composition is part of a preview.
 * Should be used to switch between preview UI states and Hilt view models.
 */
val LocalPreview: ProvidableCompositionLocal<Boolean> = compositionLocalOf { false }

@ExperimentalResourceApi
@Composable
fun previewablePainterResource(res: String): Painter = if (
    LocalPreview.current
) {
    rememberVectorPainter(Icons.Default.Warning)
} else {
    painterResource(res)
}
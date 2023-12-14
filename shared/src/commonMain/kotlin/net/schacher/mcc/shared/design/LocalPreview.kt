package net.schacher.mcc.shared.design

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

/**
 * Composition local that is used to determine if the current composition is part of a preview.
 * Should be used to switch between preview UI states and Hilt view models.
 */
val LocalPreview: ProvidableCompositionLocal<Boolean> = compositionLocalOf { false }


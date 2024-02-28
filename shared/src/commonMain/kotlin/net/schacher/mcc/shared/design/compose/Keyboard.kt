package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

/**
 * Returns true if the keyboard is visible, false otherwise.
 * The bottom inset should be greater than 10 to be considered visible.
 * 0 used to work but it's not reliable anymore since the compose multiplatform 1.6.0 release.
 **/
@Composable
fun isKeyboardVisible(): Boolean = WindowInsets.ime.getBottom(LocalDensity.current) > 10
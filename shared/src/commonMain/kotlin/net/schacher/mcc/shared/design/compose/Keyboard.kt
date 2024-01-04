package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

@Composable
fun isKeyboardVisible(): Boolean = WindowInsets.ime.getBottom(LocalDensity.current) > 0
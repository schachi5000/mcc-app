package net.schacher.mcc.shared.design.compose

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Can be empty since there is no back interaction in iOS
}
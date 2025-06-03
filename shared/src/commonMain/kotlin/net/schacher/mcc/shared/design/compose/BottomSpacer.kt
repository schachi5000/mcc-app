package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier.height(88.dp))
}
package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheetContainer(
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colors.background,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
    ) {
        Spacer(Modifier.height(8.dp))
        BottomSheetHandle()
        Spacer(Modifier.height(8.dp))
        content()
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun ColumnScope.BottomSheetHandle() {
    Row(
        modifier = Modifier.size(40.dp, 4.dp)
            .background(
                MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                RoundedCornerShape(4.dp)
            )
            .align(CenterHorizontally)
    ) {}
}
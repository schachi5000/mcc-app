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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

@Composable
fun BottomSheetContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
    ) {
        Spacer(Modifier.height(8.dp))
        BottomSheetHandle()
        Spacer(Modifier.height(8.dp))
        content()
        Spacer(Modifier.navigationBarsPadding().height(8.dp))
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Modifier.blurByBottomSheet(bottomSheetState: ModalBottomSheetState) = this.blur(
    when (bottomSheetState.targetValue) {
        ModalBottomSheetValue.Hidden -> 0.dp
        else -> bottomSheetState.progress * 4.dp
    }
)

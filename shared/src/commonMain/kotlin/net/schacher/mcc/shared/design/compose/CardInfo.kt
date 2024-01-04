package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.model.Card

@Composable
fun CardInfo(card: Card) {

    Box() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            card = card
        )

        Column(
            modifier = Modifier.padding(top = 120.dp)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to MaterialTheme.colors.background.copy(alpha = 0.1f),
                            0.3f to MaterialTheme.colors.background.copy(alpha = 1f),
                            1f to MaterialTheme.colors.background.copy(alpha = 1f)
                        )
                    )
                ),
        ) {
        
        }
    }

}
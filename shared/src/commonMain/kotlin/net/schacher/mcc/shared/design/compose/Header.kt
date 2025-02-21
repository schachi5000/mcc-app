package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp

@Composable
fun Header(title: String) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = title,
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.onBackground,
        )
    }
}

@Composable
fun HeaderSmall(
    title: String,
    subTitle: String? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = title,
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h5.copy(
                shadow = Shadow(
                    color = MaterialTheme.colors.background,
                    offset = Offset.Zero,
                    blurRadius = 16.dp.value
                )
            ),
        )

        subTitle?.let {
            Text(
                modifier = Modifier.alignByBaseline().padding(start = 8.dp),
                text = it,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h6.copy(
                    shadow = Shadow(
                        color = MaterialTheme.colors.background,
                        offset = Offset.Zero,
                        blurRadius = 16.dp.value
                    )
                ),
            )
        }
    }
}
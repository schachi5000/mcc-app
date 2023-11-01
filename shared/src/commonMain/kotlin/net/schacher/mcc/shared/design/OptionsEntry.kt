package net.schacher.mcc.shared.design

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun OptionsEntry(label: String, imageVector: ImageVector, onClick: () -> Unit) {
    OptionsEntry(
        label = label,
        icon = {
            Icon(
                imageVector = imageVector,
                contentDescription = label,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(18.dp)
            )
        },
        onClick = onClick
    )
}

@Composable
fun OptionsEntry(label: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Text(label)
    }
}
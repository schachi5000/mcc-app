package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Header(
    title: String,
    subTitle: String? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = title,
            style = MaterialTheme.typography.h5,
        )

        subTitle?.let {
            Text(
                modifier = Modifier.alignByBaseline().padding(start = 8.dp),
                text = it,
                style = MaterialTheme.typography.h6,
            )
        }
    }
}

@Composable
fun MainHeader(title: String) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = title,
            style = MaterialTheme.typography.h4
        )
    }
}

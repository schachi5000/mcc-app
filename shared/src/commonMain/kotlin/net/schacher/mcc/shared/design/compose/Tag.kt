package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Tag(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = Color.White,
    color: Color = if (MaterialTheme.colors.isLight) {
        Color.Gray
    } else {
        Color.DarkGray
    }
) {
    Surface(
        modifier = modifier.widthIn(max = 140.dp),
        shape = RoundedCornerShape(6.dp),
        color = color,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            text = text,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}
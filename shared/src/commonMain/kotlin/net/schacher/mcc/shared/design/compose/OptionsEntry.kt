package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.design.theme.DefaultShape
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


@Composable
fun OptionsGroup(title: String = "", content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colors.surface),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        if (title.isNotBlank()) {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
                text = title,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface,
                maxLines = 1,
            )
        }

        content()
    }
}

@Composable
fun OptionsEntry(label: String, imageVector: ImageVector, onClick: (() -> Unit)? = null) {
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
fun OptionsEntry(label: String, icon: @Composable () -> Unit, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null, onClick = { onClick?.invoke() })
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Text(
            text = label,
            maxLines = 1,
            style = MaterialTheme.typography.body1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
        )
    }
}

@Composable
fun OptionsGridEntry(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colors.surface,
                shape = DefaultShape
            )
            .clickable(enabled = onClick != null, onClick = { onClick?.invoke() })
            .padding(ContentPadding),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
            maxLines = 3
        )

        Text(
            modifier = Modifier.padding(top = ContentPadding),
            text = value,
            maxLines = 2,
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.onSurface,
        )
    }
}
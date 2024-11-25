package net.schacher.mcc.shared.screens.main

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import net.schacher.mcc.shared.design.compose.ButtonState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun RowScope.DefaultBottomNavigationItem(
    label: String? = null,
    drawableResource: DrawableResource,
    selectedDrawableResource: DrawableResource = drawableResource,
    color: Color = MaterialTheme.colors.primary,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (selected) 1.1f else 1f)

    BottomNavigationItem(
        icon = {
            Icon(
                painter = painterResource(
                    if (selected) {
                        selectedDrawableResource
                    } else {
                        drawableResource
                    }
                ),
                contentDescription = drawableResource.toString(),
                modifier = Modifier.scale(scale)
            )
        },
        label = label?.let { { Text(text = it, maxLines = 1) } },
        selected = selected,
        onClick = { onClick() },
        selectedContentColor = color,
        unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
    )
}

@Composable
internal fun RowScope.DefaultBottomNavigationItem(
    label: String? = null,
    imageVector: ImageVector,
    selectedImageVector: ImageVector = imageVector,
    color: Color = MaterialTheme.colors.primary,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (selected) 1.1f else 1f)

    BottomNavigationItem(
        icon = {
            Icon(
                imageVector = if (selected) {
                    selectedImageVector
                } else {
                    imageVector
                },
                contentDescription = imageVector.toString(),
                modifier = Modifier.scale(scale)
            )
        },
        label = label?.let { { Text(text = it, maxLines = 1) } },
        selected = selected,
        onClick = { onClick() },
        selectedContentColor = color,
        unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.75f)
    )
}
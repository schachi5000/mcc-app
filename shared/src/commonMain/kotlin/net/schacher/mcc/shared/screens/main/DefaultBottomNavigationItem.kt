package net.schacher.mcc.shared.screens.main

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun RowScope.DefaultBottomNavigationItem(
    label: String,
    icon: DrawableResource,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    BottomNavigationItem(
        icon = { Icon(painterResource(icon), icon.toString()) },
        label = { Text(text = label) },
        selected = selected,
        onClick = { onClick() },
        selectedContentColor = color,
        unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.75f)
    )
}

@Composable
internal fun RowScope.DefaultBottomNavigationItem(
    label: String,
    icon: @Composable () -> Unit,
    color: Color,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    BottomNavigationItem(
        icon = icon,
        label = { Text(text = label, maxLines = 1) },
        selected = selected,
        onClick = { onClick() },
        selectedContentColor = color,
        unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.75f)
    )
}
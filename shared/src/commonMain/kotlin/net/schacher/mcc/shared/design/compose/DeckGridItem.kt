package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.model.Card


@Composable
fun GridItem(
    card: Card,
    title: String? = null,
    modifier: Modifier = Modifier,
    iconOffset: DpOffset? = null,
    onClick: () -> Unit
) {
    var blur by remember { mutableStateOf(0.dp) }

    Column(
        modifier = modifier
            .bounceClick { state -> blur = 4.dp.takeIf { state == ButtonState.Pressed } ?: 0.dp }
            .noRippleClickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Thumbnail(
            card = card,
            modifier = Modifier.fillMaxWidth().blur(blur),
            offset = iconOffset
        )

        title?.let {
            Column(
                modifier = Modifier.fillMaxWidth().padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}

fun LazyGridScope.maxSpanItem(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}
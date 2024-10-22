package net.schacher.mcc.shared.design.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.design.theme.ContentPadding

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerHeader(
    pageLabels: List<String>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 25.sp,
    onLabelClick: (Int) -> Unit
) {
    val selectedItem = pagerState.currentPage
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val offset = with(LocalDensity.current) { 2 * ContentPadding.toPx() }.toInt()

    scope.launch {
        state.animateScrollToItem(selectedItem, if (selectedItem > 0) -offset else 0)
    }

    LazyRow(
        modifier = modifier,
        state = state,
        userScrollEnabled = false
    ) {
        pageLabels.forEachIndexed { index, it ->
            item {
                val selected = selectedItem == index
                val alpha: Float by animateFloatAsState(
                    targetValue = if (selected) 1f else 0.35f,
                    animationSpec = tween()
                )

                Text(
                    text = it,
                    fontSize = fontSize,
                    color = MaterialTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(
                            start = if (index == 0) ContentPadding else 0.dp,
                            end = if (index == pageLabels.size - 1) 200.dp else 12.dp
                        )
                        .noRippleClickable { onLabelClick(index) }
                        .alpha(alpha),
                )
            }
        }
    }
}

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
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground,
        )

        subTitle?.let {
            Text(
                modifier = Modifier.alignByBaseline().padding(start = 8.dp),
                text = it,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
            )
        }
    }
}
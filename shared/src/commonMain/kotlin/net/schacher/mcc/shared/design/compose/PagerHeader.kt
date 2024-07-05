package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    fontSize: TextUnit = 24.sp,
    onLabelClick: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    val selectedItem = pagerState.currentPage
    val state = rememberLazyListState()

    scope.launch {
        if (selectedItem > 0) {
            state.scrollToItem(selectedItem - 1, 0)
        } else {
            state.scrollToItem(selectedItem, 0)
        }
    }

    LazyRow(
        modifier = modifier,
        state = state,
        userScrollEnabled = false
    ) {
        pageLabels.forEachIndexed { index, it ->
            item {
                val selected = selectedItem == index

                Text(
                    text = it,
                    fontSize = fontSize,
                    color = MaterialTheme.colors.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(
                            start = if (index == 0) ContentPadding else 0.dp,
                            end = 12.dp
                        )
                        .noRippleClickable { onLabelClick(index) }
                        .alpha(if (selected) 1f else 0.35f),
                )
            }
        }
    }
}
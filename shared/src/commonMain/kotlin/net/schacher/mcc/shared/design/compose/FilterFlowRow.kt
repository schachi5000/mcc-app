package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.theme.color
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.screens.search.Filter
import net.schacher.mcc.shared.screens.search.Filter.Type.AGGRESSION
import net.schacher.mcc.shared.screens.search.Filter.Type.JUSTICE
import net.schacher.mcc.shared.screens.search.Filter.Type.LEADERSHIP
import net.schacher.mcc.shared.screens.search.Filter.Type.PROTECTION
import net.schacher.mcc.shared.screens.search.SearchFilterChip
import net.schacher.mcc.shared.screens.search.label

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterFlowRow(
    modifier: Modifier = Modifier,
    filters: Set<Filter>,
    onFilterClicked: (Filter) -> Unit = {}
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        filters.forEach { filter ->
            SearchFilterChip(
                color = when (filter.type) {
                    AGGRESSION -> Aspect.AGGRESSION.color
                    PROTECTION -> Aspect.PROTECTION.color
                    JUSTICE -> Aspect.JUSTICE.color
                    LEADERSHIP -> Aspect.LEADERSHIP.color
                    else -> MaterialTheme.colors.primary
                },
                label = filter.type.label,
                selected = filter.active
            ) {
                onFilterClicked(filter)
            }
        }
    }
}

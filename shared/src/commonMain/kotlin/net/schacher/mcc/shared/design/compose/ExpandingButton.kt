package net.schacher.mcc.shared.design.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.theme.ButtonSize
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.design.theme.FABPadding

@Composable
fun BoxScope.ExpandingButton(
    label: String,
    expanded: Boolean,
    alignment: Alignment = Alignment.BottomEnd,
    paddingBottom: Dp = ContentPadding,
    icon: @Composable() () -> Unit,
    onClick: () -> Unit
) {
    var horizontalBias by remember { mutableStateOf(1f) }
    val horizontalAlignment by animateHorizontalAlignmentAsState(horizontalBias)

    var endPadding by remember { mutableStateOf(FABPadding) }
    val animateEndPadding by animateDpAsState(endPadding)

    horizontalBias = if (expanded) 0f else 1f
    endPadding = if (expanded) 0.dp else FABPadding

    Column(
        modifier = Modifier.fillMaxWidth()
            .align(alignment)
            .padding(
                end = animateEndPadding,
                bottom = paddingBottom
            ),
        horizontalAlignment = horizontalAlignment
    ) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.sizeIn(
                minWidth = ButtonSize,
                maxHeight = ButtonSize,
            ),
            contentColor = MaterialTheme.colors.onPrimary,
            backgroundColor = MaterialTheme.colors.primary,
            shape = DefaultShape
        ) {
            Row(
                modifier = Modifier.fillMaxHeight().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()

                AnimatedVisibility(visible = expanded) {
                    Text(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        text = label
                    )
                }
            }
        }
    }
}

@Composable
private fun animateHorizontalAlignmentAsState(targetBiasValue: Float): State<BiasAlignment.Horizontal> {
    val bias by animateFloatAsState(targetBiasValue)
    return derivedStateOf { BiasAlignment.Horizontal(bias) }
}
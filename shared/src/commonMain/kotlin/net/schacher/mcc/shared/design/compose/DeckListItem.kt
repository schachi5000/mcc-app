package net.schacher.mcc.shared.design.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.model.Deck

@Composable
fun DeckListItem(
    deck: Deck, onClick: () -> Unit
) {
    Row(
        modifier = Modifier.bounceClick().noRippleClickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Thumbnail(
            modifier = Modifier.size(80.dp),
            card = deck.hero
        )

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = deck.name, style = MaterialTheme.typography.h6, maxLines = 2
            )

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = listOf(
                    deck.aspect?.name ?: "Unknown",
                    "${deck.cards.size} Cards",
                    "${deck.requiredDecks.size} Packs"
                ).joinToString(" · "),
                style = MaterialTheme.typography.body2,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

@Composable
fun LoadingDeckListItem() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ShimmerBox(
            modifier = Modifier.size(80.dp)
                .clip(DefaultShape)
        )

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            ShimmerBox(
                modifier = Modifier.fillMaxWidth(0.6f)
                    .height(22.dp)
                    .clip(DefaultShape)
            )

            Spacer(Modifier.height(6.dp))

            ShimmerBox(
                modifier = Modifier.fillMaxWidth(0.9f)
                    .height(22.dp)
                    .clip(DefaultShape)
            )
        }
    }
}

enum class ButtonState { Pressed, Idle }

fun Modifier.bounceClick(): Modifier = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) 0.95f else 1f)

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.clickable(interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = { }).pointerInput(buttonState) {
        awaitPointerEventScope {
            buttonState = if (buttonState == ButtonState.Pressed) {
                waitForUpOrCancellation()
                ButtonState.Idle
            } else {
                awaitFirstDown(false)
                ButtonState.Pressed
            }
        }
    }
}


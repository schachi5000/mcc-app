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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.cards
import marvelchampionscompanion.shared.generated.resources.decks
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.localization.label
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck
import org.jetbrains.compose.resources.pluralStringResource

@Composable
fun DeckListItem(
    modifier: Modifier = Modifier,
    deck: Deck,
    onClick: () -> Unit
) {
    ListItem(
        modifier = modifier,
        card = deck.hero,
        title = deck.name,
        subtitle = listOfNotNull(
            deck.aspect?.label,
            pluralStringResource(
                Res.plurals.cards,
                deck.cards.size,
                deck.cards.size
            ),
            pluralStringResource(
                Res.plurals.decks,
                deck.requiredPacks.size,
                deck.requiredPacks.size
            )
        ).joinToString(" · "),
        onClick = onClick
    )
}

@Composable
fun CardListItem(
    card: Card,
    onClick: () -> Unit
) {
    ListItem(
        card = card,
        title = card.name,
        subtitle = card.type?.label,
        onClick = onClick
    )
}

@Composable
fun ListItem(
    card: Card,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.bounceClick().noRippleClickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Thumbnail(
            modifier = Modifier.size(80.dp),
            card = card
        )

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                maxLines = 2,
                color = MaterialTheme.colors.onBackground
            )

            subtitle?.let {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = it,
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onBackground,
                    maxLines = 2
                )
            }
        }
    }
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
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
                modifier = Modifier.fillMaxWidth(0.7f)
                    .height(22.dp)
                    .clip(DefaultShape)
            )

            Spacer(Modifier.height(6.dp))

            ShimmerBox(
                modifier = Modifier.fillMaxWidth(1f)
                    .height(22.dp)
                    .clip(DefaultShape)
            )
        }
    }
}

enum class ButtonState { Pressed, Idle }

fun Modifier.bounceClick(onButtonStateChanged: (ButtonState) -> Unit = {}): Modifier = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) 0.95f else 1f)

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = { }
    ).pointerInput(buttonState) {
        awaitPointerEventScope {
            buttonState = if (buttonState == ButtonState.Pressed) {
                waitForUpOrCancellation()
                onButtonStateChanged(ButtonState.Idle)
                ButtonState.Idle
            } else {
                awaitFirstDown(false)
                onButtonStateChanged(ButtonState.Pressed)
                ButtonState.Pressed
            }
        }
    }
}


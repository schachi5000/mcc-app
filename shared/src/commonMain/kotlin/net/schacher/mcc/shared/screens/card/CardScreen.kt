package net.schacher.mcc.shared.screens.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import net.schacher.mcc.shared.design.compose.BackButton
import net.schacher.mcc.shared.design.compose.Card
import net.schacher.mcc.shared.design.theme.color
import net.schacher.mcc.shared.localization.localize
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType

@Composable
fun CardScreen(modifier: Modifier = Modifier, card: Card, onCloseClick: () -> Unit) {
    Logger.i { card.toString() }

    Box(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth()
                .blur(0.5.dp)
                .graphicsLayer { translationY = getTranslationY(card).toPx() },
            card = card
        )

        Tag(
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(16.dp)
                .alpha(0.8f),
            text = card.code
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to MaterialTheme.colors.background.copy(alpha = 0f),
                            0.15f to MaterialTheme.colors.background.copy(alpha = 0.2f),
                            0.3f to MaterialTheme.colors.background.copy(alpha = 0.8f),
                            0.4f to MaterialTheme.colors.background.copy(alpha = 1f),
                            1f to MaterialTheme.colors.background.copy(alpha = 1f)
                        )
                    )
                )
                .padding(top = 200.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = card.name,
                maxLines = 2,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp,
                color = MaterialTheme.colors.onSurface
            )

            Text(
                text = card.packName,
                maxLines = 1,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.75f)
            )


            LazyRow(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item { card.type?.let { Tag(text = it.localize()) } }
                item { card.aspect?.let { Tag(text = it.localize(), color = it.color) } }
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                card.traits?.let {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = it.toAnnotatedString(),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.onSurface
                    )
                }

                card.text?.let {
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = it.toAnnotatedString(),
                        fontSize = 18.sp,
                        color = MaterialTheme.colors.onSurface
                    )
                }

                card.attackText?.let {
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = it.toAnnotatedString(),
                        fontSize = 18.sp,
                        color = MaterialTheme.colors.onSurface
                    )
                }

                card.boostText?.let {
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = buildAnnotatedString {
                            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                            append("Boost: ")
                            pop()
                            append(it.toAnnotatedString())
                        },
                        fontSize = 18.sp,
                        color = MaterialTheme.colors.onSurface
                    )
                }

                card.quote?.let {
                    Text(
                        modifier = Modifier.padding(top = 32.dp),
                        text = it,
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
        }

        BackButton(onCloseClick)
    }
}

private fun getTranslationY(card: Card): Dp = when (card.type) {
    CardType.EVENT,
    CardType.MINION,
    CardType.VILLAIN,
    CardType.ENVIRONMENT,
    CardType.SUPPORT,
    CardType.UPGRADE,
    CardType.ALLY,
    CardType.OBLIGATION,
    CardType.TREACHERY,
    CardType.HERO -> (-65).dp

    else -> 0.dp
}

@Composable
private fun Tag(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = Color.White,
    color: Color = if (MaterialTheme.colors.isLight) {
        Color.Gray
    } else {
        Color.DarkGray
    }
) {
    Text(
        modifier = modifier
            .widthIn(max = 140.dp)
            .background(color = color, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        text = text,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = textColor
    )
}

private val boldRegex = Regex("<b>(.*?)</b>|\\[\\[(.*?)]]|\\[(.*?)]")

private val italicRegex = Regex("<i>(.*?)</i>")

private const val EMOJI_HERO = "\uD83D\uDE42"

private fun String.toAnnotatedString(): AnnotatedString {
    val value = this

    // Could be handled more elegantly, but this works for now
    val boldStrings = boldRegex.findAll(value)
        .map {
            it.value
                .replace("<b>", "")
                .replace("</b>", "")
                .replace("[", "")
                .replace("]", "")
                .replace("per_hero", EMOJI_HERO)
                .replace("per_player", EMOJI_HERO)
        }
        .toList()

    return buildAnnotatedString {
        value.split(boldRegex).forEachIndexed { index, s ->
            append(s)

            if (index < boldStrings.count()) {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append(boldStrings[index])
                pop()
            }
        }
    }
}
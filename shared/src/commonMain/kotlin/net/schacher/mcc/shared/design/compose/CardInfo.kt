package net.schacher.mcc.shared.design.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.schacher.mcc.shared.design.theme.color
import net.schacher.mcc.shared.localization.localize
import net.schacher.mcc.shared.model.Card

@Composable
fun CardInfo(card: Card) {

    Box() {
        Card(
            modifier = Modifier.fillMaxWidth().blur(2.dp),
            card = card
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to MaterialTheme.colors.background.copy(alpha = 0.1f),
                            0.3f to MaterialTheme.colors.background.copy(alpha = 0.8f),
                            0.4f to MaterialTheme.colors.background.copy(alpha = 1f),
                            1f to MaterialTheme.colors.background.copy(alpha = 1f)
                        )
                    )
                )
                .padding(top = 200.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.name,
                textAlign = TextAlign.Center,
                maxLines = 2,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp,
                color = MaterialTheme.colors.onSurface
            )

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Tag(text = card.code)

                card.type?.let {
                    Tag(text = it.localize())
                }

                card.aspect?.let {
                    Tag(text = it.localize(), color = it.color)
                }

                card.packName.let {
                    Tag(text = it)
                }
            }

            card.text?.let {
                Text(
                    modifier = Modifier.padding(top = 24.dp),
                    text = it.toAnnotatedString(),
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.75f)
                )
            }

            card.boostText?.let {
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "<b>Boost</b>: $it".toAnnotatedString(),
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
private fun Tag(
    text: String, color: Color = if (MaterialTheme.colors.isLight) {
        Color.Gray
    } else {
        Color.DarkGray
    }
) {
    Text(
        modifier = Modifier
            .widthIn(max = 112.dp)
            .background(color = color, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        text = text,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = Color.White
    )
}

private val boldRegex = Regex("<b>(.*?)</b>|\\[\\[(.*?)]]|\\[(.*?)]")

private val italicRegex = Regex("<i>(.*?)</i>")

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
        }
        .toList()


    return buildAnnotatedString {
        value.split(boldRegex).forEachIndexed() { index, s ->
            append(s)

            if (index < boldStrings.count()) {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append(boldStrings[index])
                pop()
            }
        }
    }
}


package net.schacher.mcc.shared.screens.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.schacher.mcc.shared.design.compose.BackButton
import net.schacher.mcc.shared.design.compose.BottomSpacer
import net.schacher.mcc.shared.design.compose.Card
import net.schacher.mcc.shared.design.compose.CardBackgroundBox
import net.schacher.mcc.shared.design.compose.SecondaryButton
import net.schacher.mcc.shared.design.compose.Tag
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.design.theme.color
import net.schacher.mcc.shared.localization.label
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.screens.AppRoute
import net.schacher.mcc.shared.screens.navigate
import net.schacher.mcc.shared.screens.resultState
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun CardScreen(
    cardCode: String,
    modifier: Modifier = Modifier,
    viewModel: CardScreenViewModel = koinInject { parametersOf(cardCode) },
    navController: NavController = koinInject()
) {

    val selectedDeckId = navController.resultState<Int?>()?.value
    selectedDeckId?.let {
        LaunchedEffect(it) {
            viewModel.onAddCardToDeck(it, cardCode)
        }
    }

    val state = viewModel.state.collectAsState()
    CardScreen(
        card = state.value.card,
        modifier = modifier,
        onCloseClick = { navController.popBackStack() },
        onLinkedCardClick = { navController.navigate("card/${it}") },
        onAddToDeckClick = { navController.navigate(AppRoute.SelectDeck) }
            .takeIf { state.value.canAddToDeck }
    )
}

@Composable
fun CardScreen(
    card: Card,
    modifier: Modifier = Modifier,
    onAddToDeckClick: (() -> Unit)?,
    onLinkedCardClick: (String) -> Unit,
    onCloseClick: () -> Unit,
) {
    CardBackgroundBox(
        cardCode = card.code,
        modifier = modifier
    ) {
        Content(
            card = card,
            onAddToDeckClick = onAddToDeckClick,
            onCloseClick = onCloseClick,
            onLinkedCardClick = onLinkedCardClick
        )
    }
}

@Composable
private fun Content(
    card: Card,
    onAddToDeckClick: (() -> Unit)?,
    onCloseClick: () -> Unit,
    onLinkedCardClick: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val state = rememberLazyListState()

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = ContentPadding),
            state = state,
            verticalArrangement = Arrangement.spacedBy(ContentPadding)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = ContentPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        card = card,
                        parallaxEffect = true,
                        modifier = Modifier.sizeIn(maxWidth = 260.dp)
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = card.name,
                        maxLines = 2,
                        style = MaterialTheme.typography.h5,
                        color = MaterialTheme.colors.onSurface
                    )

                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = card.packName,
                        maxLines = 1,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.75f)
                    )

                    LazyRow(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item { card.aspect?.let { Tag(text = it.label, color = it.color) } }
                        item { Tag(text = card.code) }
                        item { card.type?.let { Tag(text = it.label) } }
                    }
                }
            }

            card.traits?.let {
                item {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = it.toAnnotatedString(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }

            card.text?.let {
                item {
                    Text(
                        text = it.toAnnotatedString(),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }

            card.attackText?.let {
                item {
                    Text(
                        text = it.toAnnotatedString(),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }

            card.boostText?.let {
                item {
                    Text(
                        text = buildAnnotatedString {
                            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                            append("Boost: ")
                            pop()
                            append(it.toAnnotatedString())
                        },
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
            
            card.quote?.let {
                item {
                    Text(
                        modifier = Modifier.padding(top = ContentPadding),
                        text = it,
                        style = MaterialTheme.typography.body1,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }

            card.linkedCard?.let {
                item {
                    SecondaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onLinkedCardClick(it.code) },
                        label = it.name
                    )
                }
            }

            onAddToDeckClick?.let {
                item {
                    SecondaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = it,
                        label = "Add to deck"
                    )
                }
            }

            item {
                BottomSpacer(Modifier.navigationBarsPadding())
            }
        }

        BackButton {
            onCloseClick()
        }
    }
}


private val boldRegex = Regex("<b>(.*?)</b>|\\[\\[(.*?)]]|\\[(.*?)]")

private val italicRegex = Regex("<i>(.*?)</i>")

private const val EMOJI_HERO = "\uD83D\uDE42"

private fun String.toAnnotatedString(): AnnotatedString {
    val value = this

    // Could be handled more elegantly, but this works for now
    val boldStrings = boldRegex.findAll(value).map {
        it.value.replace("<b>", "")
            .replace("</b>", "")
            .replace("[", "")
            .replace("]", "")
            .replace("per_hero", EMOJI_HERO)
            .replace("per_player", EMOJI_HERO)
    }.toList()

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
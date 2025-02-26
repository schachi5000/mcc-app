package net.schacher.mcc.shared.screens.deck

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.delete
import marvelchampionscompanion.shared.generated.resources.hero_cards
import marvelchampionscompanion.shared.generated.resources.other_cards
import marvelchampionscompanion.shared.generated.resources.remove_card_from_deck
import net.schacher.mcc.shared.design.compose.BackButton
import net.schacher.mcc.shared.design.compose.BackHandler
import net.schacher.mcc.shared.design.compose.BottomSheetContainer
import net.schacher.mcc.shared.design.compose.BottomSpacer
import net.schacher.mcc.shared.design.compose.Card
import net.schacher.mcc.shared.design.compose.CardBackgroundBox
import net.schacher.mcc.shared.design.compose.CardRow
import net.schacher.mcc.shared.design.compose.CardRowEntry
import net.schacher.mcc.shared.design.compose.ConfirmationDialog
import net.schacher.mcc.shared.design.compose.HeaderSmall
import net.schacher.mcc.shared.design.compose.LabeledCard
import net.schacher.mcc.shared.design.compose.ProgressDialog
import net.schacher.mcc.shared.design.compose.SecondaryButton
import net.schacher.mcc.shared.design.compose.Tag
import net.schacher.mcc.shared.design.compose.noRippleClickable
import net.schacher.mcc.shared.design.theme.BottomSheetColors
import net.schacher.mcc.shared.design.theme.BottomSheetShape
import net.schacher.mcc.shared.design.theme.ContentPadding
import net.schacher.mcc.shared.design.theme.DefaultShape
import net.schacher.mcc.shared.design.theme.color
import net.schacher.mcc.shared.localization.label
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.screens.AppRoute
import net.schacher.mcc.shared.screens.deck.DeckScreenViewModel.UiState
import net.schacher.mcc.shared.screens.deck.DeckScreenViewModel.UiState.CardOption.REMOVE
import net.schacher.mcc.shared.screens.deck.DeckScreenViewModel.UiState.DeckOption.DELETE
import net.schacher.mcc.shared.screens.deck.DeckScreenViewModel.UiState.Loading.DeletingDeck
import net.schacher.mcc.shared.screens.deck.DeckScreenViewModel.UiState.Loading.RemovingCard
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

private const val COLUMN_COUNT = 3

@Composable
fun DeckScreen(
    deckId: Int,
    navController: NavController = koinInject(),
    viewModel: DeckScreenViewModel = koinInject { parametersOf(deckId) },
) {
    val state = viewModel.state.collectAsState().value

    if (state == null) {
        navController.popBackStack()
        return
    }

    DeckScreen(
        state = state,
        navController = navController,
        onShowCardOptions = { viewModel.onShowCardOptionClicked(it) },
        onCardOptionsDismiss = { viewModel.onCardOptionDismissed() },
        onOptionClick = { viewModel.onOptionClicked(it) },
        onDeleteDeckClicked = { viewModel.onDeleteDeckClicked() },
    )
}

@Composable
fun DeckScreen(
    state: UiState,
    navController: NavController,
    onShowCardOptions: (Card) -> Unit,
    onCardOptionsDismiss: () -> Unit,
    onOptionClick: (UiState.CardOption) -> Unit,
    onDeleteDeckClicked: () -> Unit,
) {

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    var deleteDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    LaunchedEffect(state.selectedCard) {
        if (state.selectedCard != null) {
            scope.launch { sheetState.show() }
        } else {
            scope.launch { sheetState.hide() }
        }
    }

    BackHandler(sheetState.isVisible) {
        onCardOptionsDismiss()
    }

    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.currentValue }
            .collect {
                if (it == ModalBottomSheetValue.Hidden) {
                    onCardOptionsDismiss()
                }
            }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = BottomSheetShape,
        sheetBackgroundColor = BottomSheetColors.Background,
        scrimColor = BottomSheetColors.Scrim,
        sheetContent = { OptionBottomSheet(state.cardOptions, onOptionClick) }
    ) {
        Content(
            state = state,
            onCloseClick = { navController.popBackStack() },
            onCardClick = { navController.navigate(AppRoute.toCard(it.code)) },
            onCardOptionsClick = onShowCardOptions,
            onDeleteDeckClicked = { deleteDialog = true }
        )
    }

    if (deleteDialog) {
        ConfirmationDialog(title = state.deck.name,
            message = "Soll das Deck unwiederruflich gelöscht werden?",
            onDismiss = { deleteDialog = false },
            onConfirm = {
                deleteDialog = false
                onDeleteDeckClicked()
            })
    }

    val loading = state.loading
    if (loading != null) {
        ProgressDialog(
            title = when (loading) {
                DeletingDeck -> "Deleting Deck"
                RemovingCard -> "Removing Card"
            },
            dismissible = false
        )
    }
}

@Composable
private fun Content(
    state: UiState,
    onCloseClick: () -> Unit,
    onCardClick: (Card) -> Unit,
    onCardOptionsClick: (Card) -> Unit,
    onDeleteDeckClicked: () -> Unit,
) {
    CardBackgroundBox(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        cardCode = state.deck.hero.code,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(ContentPadding)
        ) {
            item { HeroSection(state, onCardClick) }
            item {
                Column(Modifier.padding(horizontal = ContentPadding)) {
                    TitleSection(state)
                    TagSection(state)
                }
            }

            if (state.deck.description != null) {
                item { DescriptionSection(state.deck.description) }
            }

            item { HeroCardsSection(state, onCardClick) }
            item { OtherCardsSection(state, onCardClick, onCardOptionsClick) }

            state.deckOptions.find { it == DELETE }?.let {
                item {
                    SecondaryButton(
                        modifier = Modifier
                            .padding(
                                start = ContentPadding,
                                end = ContentPadding,
                                top = ContentPadding
                            )
                            .fillMaxWidth(),
                        onClick = onDeleteDeckClicked,
                        label = UiState.DeckOption.DELETE.label
                    )
                }
            }

            item {
                BottomSpacer()
            }
        }

        BackButton {
            onCloseClick()
        }
    }
}

@Composable
private fun OtherCardsSection(
    state: UiState,
    onCardClick: (Card) -> Unit,
    onCardOptionsClick: (Card) -> Unit
) {
    if (state.otherCards.isNotEmpty()) {

        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(ContentPadding),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HeaderSmall(
                    title = stringResource(Res.string.other_cards),
                    subTitle = state.otherCards.size.toString()
                )
            }

            val rows = state.otherCards.chunked(3)
            for (index in rows.indices) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = ContentPadding,
                            end = ContentPadding,
                            top = if (index == 0) 0.dp else 8.dp,
                            bottom = if (index <= rows.size - 1) 8.dp else 0.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rows[index].forEach { card ->
                        Box(Modifier.weight(1f)) {
                            LabeledCard(
                                modifier = Modifier.wrapContentHeight(),
                                label = card.name,
                                card = card
                            ) {
                                onCardClick(card)
                            }

                            if (state.cardOptions.isNotEmpty()) {
                                Icon(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(32.dp)
                                        .background(
                                            MaterialTheme.colors.surface.copy(alpha = 0.8f),
                                            CircleShape
                                        )
                                        .padding(4.dp)
                                        .align(Alignment.TopEnd)
                                        .clickable { onCardOptionsClick(card) },
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = UiState.CardOption.REMOVE.label,
                                    tint = MaterialTheme.colors.onSurface
                                )
                            }
                        }
                    }

                    if (rows[index].size < COLUMN_COUNT) {
                        repeat(COLUMN_COUNT - rows[index].size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LazyItemScope.HeroSection(state: UiState, onCardClick: (Card) -> Unit) {
    Row(
        modifier = Modifier.statusBarsPadding().padding(ContentPadding).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            card = state.deck.hero,
            modifier = Modifier.padding(top = ContentPadding).fillParentMaxWidth(0.60f),
            parallaxEffect = true
        ) {
            onCardClick(state.deck.hero)
        }

    }
}

@Composable
private fun TitleSection(state: UiState) {
    Column {
        Text(
            text = state.deck.name,
            maxLines = 2,
            style = MaterialTheme.typography.h5.copy(
                shadow = Shadow(
                    color = MaterialTheme.colors.background,
                    offset = Offset.Zero,
                    blurRadius = 16.dp.value
                )
            ),
            color = MaterialTheme.colors.onSurface
        )

        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = state.deck.hero.name,
            maxLines = 1,
            style = MaterialTheme.typography.h6.copy(
                shadow = Shadow(
                    color = MaterialTheme.colors.background,
                    offset = Offset.Zero,
                    blurRadius = 16.dp.value
                )
            ),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.75f)
        )
    }
}

@Composable
private fun TagSection(state: UiState) {
    LazyRow(
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        state.deck.aspect?.let {
            item { Tag(text = it.label, color = it.color) }
        }

        item { Tag(text = state.deck.id.toString()) }
        state.deck.version?.let {
            item { Tag(text = "v$it") }
        }

        state.deck.problem?.let {
            item {
                Tag(
                    modifier = Modifier.padding(start = 4.dp),
                    text = it,
                    color = MaterialTheme.colors.error
                )
            }
        }
    }
}

@Composable
private fun DescriptionSection(description: String) {
    var expandedDescription by remember { mutableStateOf(false) }
    val maxLines by animateIntAsState(
        targetValue = if (expandedDescription) 100 else 4,
        animationSpec = spring()
    )

    Column(Modifier.fillMaxWidth()
        .padding(
            start = ContentPadding,
            top = ContentPadding,
            end = ContentPadding
        )
        .background(
            color = MaterialTheme.colors.surface,
            shape = DefaultShape
        )
        .padding(ContentPadding)
        .noRippleClickable { expandedDescription = !expandedDescription })
    {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = description,
            style = MaterialTheme.typography.body1,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colors.onBackground,
        )
    }
}

@Composable
private fun HeroCardsSection(state: UiState, onCardClick: (Card) -> Unit) {
    val heroCards = CardRowEntry(
        title = stringResource(Res.string.hero_cards),
        cards = state.heroCards
    )

    CardRow(
        modifier = Modifier.padding(top = ContentPadding),
        cardRowEntry = heroCards
    ) {
        onCardClick(it)
    }
}

@Composable
private fun OptionBottomSheet(
    cardOptions: Set<UiState.CardOption>,
    onOptionClick: (UiState.CardOption) -> Unit
) {
    BottomSheetContainer {
        Column {
            cardOptions.forEach {
                Row(modifier = Modifier.fillMaxWidth()
                    .height(64.dp)
                    .clickable { onOptionClick(it) }
                    .padding(ContentPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colors.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = it.label,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(start = ContentPadding)
                    )
                }
            }
        }
    }
}

private val UiState.CardOption.label
    @Composable
    get() = when (this) {
        REMOVE -> stringResource(Res.string.remove_card_from_deck)
    }

private val UiState.DeckOption.label
    @Composable
    get() = when (this) {
        DELETE -> stringResource(Res.string.delete)
    }


private data class ConfirmationState(val title: String, val message: String)
package net.schacher.mcc.shared.screens.deck

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository

class DeckViewModel(
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DeckUiState(this.deckRepository.decks))

    val state = _state.asStateFlow()
}

data class DeckUiState(
    val result: List<Deck> = emptyList()
)


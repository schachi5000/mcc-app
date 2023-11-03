package net.schacher.mcc.shared.screens.deck

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository

class DeckViewModel(
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DeckUiState(this.deckRepository.state.value))

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            deckRepository.state.collect { value ->
                _state.value = DeckUiState(value)
            }
        }
    }
}

data class DeckUiState(val decks: List<Deck> = emptyList())


package net.schacher.mcc.shared.screens.mydecks

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository

class MyDecksViewModel(
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState(this.deckRepository.decks.value))

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            deckRepository.decks.collect { value ->
                _state.value = UiState(value)
            }
        }
    }

    fun onCreateDeckClick() {

    }

    data class UiState internal constructor(val decks: List<Deck> = emptyList())
}



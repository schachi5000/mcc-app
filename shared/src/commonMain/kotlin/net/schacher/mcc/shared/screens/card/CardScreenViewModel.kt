package net.schacher.mcc.shared.screens.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository

class CardScreenViewModel(
    cardCode: String,
    cardRepository: CardRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _state: MutableStateFlow<UiState> = runBlocking {
        MutableStateFlow(UiState(cardRepository.getCard(cardCode)))
    }

    val state = _state.asStateFlow()

    fun onAddCardToDeck(it: Int, cardCode: String) {
        this.viewModelScope.launch {
            deckRepository.addCardToDeck(it, cardCode)
        }
    }

    data class UiState internal constructor(
        val card: Card,
    )
}
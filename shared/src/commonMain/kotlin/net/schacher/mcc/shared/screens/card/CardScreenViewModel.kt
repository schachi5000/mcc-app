package net.schacher.mcc.shared.screens.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository

class CardScreenViewModel(
    cardCode: String,
    cardRepository: CardRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {

    private lateinit var _state: MutableStateFlow<UiState>

    init {
        this.viewModelScope.launch {
            val card = cardRepository.getCard(cardCode)
            _state = MutableStateFlow(UiState(card))
        }
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
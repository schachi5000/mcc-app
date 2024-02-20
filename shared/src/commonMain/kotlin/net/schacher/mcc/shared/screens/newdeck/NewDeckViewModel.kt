package net.schacher.mcc.shared.screens.newdeck

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository

class NewDeckViewModel(
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            cardRepository.cards.collect { value ->
                _state.value = UiState(value.values
                    .filter { it.type == CardType.HERO }
                    .sortedBy { it.name }
                    .toSet())
            }
        }
    }

    fun onHeroCardSelected(card: Card) {
    }

    data class UiState internal constructor(val heros: Set<Card> = emptySet())
}



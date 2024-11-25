package net.schacher.mcc.shared.screens.newdeck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
        this.viewModelScope.launch {
            cardRepository.cards.collect { value ->
                _state.value = UiState(value.values
                    .filter { it.type == CardType.HERO }
                    .sortedBy { it.name }
                    .toSet())
            }
        }
    }

    fun onHeroCardSelected(hero: Card) {
        _state.update {
            it.copy(selectedHero = hero)
        }
    }

    fun onBackPress() {
        _state.update {
            it.copy(selectedHero = null)
        }
    }

    fun onCreateDeckClicked(hero: Card) {
        this.viewModelScope.launch {
            deckRepository.createDeck(heroCard = hero)
        }
    }

    data class UiState(
        val allHeroes: Set<Card> = emptySet(),
        val selectedHero: Card? = null
    )
}



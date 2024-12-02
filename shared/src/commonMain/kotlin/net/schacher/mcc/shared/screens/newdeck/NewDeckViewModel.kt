package net.schacher.mcc.shared.screens.newdeck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                    .distinct())
            }
        }
    }

    suspend fun onCreateNewDeck(hero: Card, deckLabel: String? = null): Boolean {
        this._state.update {
            it.copy(loading = true)
        }

        return withContext(viewModelScope.coroutineContext) {
            val result = deckRepository.createDeck(hero.code, deckLabel)
            _state.update {
                it.copy(loading = false)
            }
            result.isSuccess
        }
    }

    data class UiState(
        val allHeroes: List<Card> = emptyList(),
        val loading: Boolean = false
    )
}



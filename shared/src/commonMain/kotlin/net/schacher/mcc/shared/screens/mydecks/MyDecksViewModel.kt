package net.schacher.mcc.shared.screens.mydecks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.DeckRepository

class MyDecksViewModel(private val deckRepository: DeckRepository) : ViewModel() {

    private val _state = MutableStateFlow(
        UiState(decks = this.deckRepository.decks.value)
    )

    val state = _state.asStateFlow()

    init {
        this.viewModelScope.launch {
            deckRepository.decks.collect {
                _state.update {
                    it.copy(
                        decks = it.decks,
                        refreshing = false
                    )
                }
            }
        }

        viewModelScope.launch {
            refreshDecks()
        }
    }

    fun onCreateDeckClick() {
        // TODO Not yet implemented
    }

    fun onRefreshClicked() {
        viewModelScope.launch {
            refreshDecks()
        }
    }

    private suspend fun refreshDecks() {
        _state.update { it.copy(refreshing = true) }

        try {
            deckRepository.refreshAllUserDecks()
        } catch (e: Exception) {
            Logger.e(e.toString())
        }

        _state.update {
            it.copy(
                decks = deckRepository.decks.value,
                refreshing = false
            )
        }
    }

    data class UiState internal constructor(
        val decks: List<Deck> = emptyList(),
        val refreshing: Boolean = false
    )
}



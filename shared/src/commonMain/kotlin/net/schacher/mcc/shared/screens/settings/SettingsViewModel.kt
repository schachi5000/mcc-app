package net.schacher.mcc.shared.screens.settings

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository

class SettingsViewModel(
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            cardRepository.cards.size,
            deckRepository.decks.size
        )
    )

    val state = _uiState.asStateFlow()

    fun onWipeDatabaseClick() {
        if (state.value.syncInProgress) {
            return
        }

        viewModelScope.launch {
            Logger.d { "Wiping database..." }
            cardRepository.deleteAllCards()
            deckRepository.deleteAllDecks()
            Logger.d { "Wiping complete" }

            _uiState.update { it.copy(cardCount = 0, deckCount = 0) }
        }
    }

    fun onSyncClick() {
        _uiState.update { it.copy(syncInProgress = true) }

        viewModelScope.launch {
            cardRepository.refresh()

            _uiState.update {
                it.copy(
                    cardCount = cardRepository.cards.size,
                    deckCount = deckRepository.decks.size,
                    syncInProgress = false
                )
            }
        }
    }
}

data class SettingsUiState(val cardCount: Int, val deckCount: Int, val syncInProgress: Boolean = false)
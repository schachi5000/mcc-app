package net.schacher.mcc.shared.screens.mydecks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.AppLogger
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.AuthRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.utils.launchAndCollect

class MyDecksViewModel(
    private val deckRepository: DeckRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private companion object {
        const val TAG = "MyDecksViewModel"
    }

    private val _state = MutableStateFlow(this.createState())

    val state = _state.asStateFlow()

    init {
        this.viewModelScope.launchAndCollect(this.deckRepository.decks) {
            updateState()
        }

        this.viewModelScope.launchAndCollect(this.authRepository.loginState) {
            updateState()
        }
    }

    private fun updateState() {
        _state.update { createState() }
    }

    private fun createState(): UiState = UiState(
        decks = this.deckRepository.decks.value,
        refreshing = false,
        allowLogIn = !this.authRepository.isSignedInAsUser()
    )

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
            AppLogger.e(e.toString())
        }

        _state.update {
            it.copy(
                decks = deckRepository.decks.value,
                refreshing = false
            )
        }
    }

    data class UiState(
        val decks: List<Deck> = emptyList(),
        val refreshing: Boolean = false,
        val allowLogIn: Boolean = false
    )
}



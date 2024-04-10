package net.schacher.mcc.shared.screens.mydecks

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.auth.AuthHandler
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import kotlin.time.Duration.Companion.seconds

class MyDecksViewModel(
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
    private val authHandler: AuthHandler
) : ViewModel() {

    private val _state = MutableStateFlow(
        UiState(
            decks = this.deckRepository.decks.value,
            canCreateDecks = authHandler.loggedIn
        )
    )

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            deckRepository.decks.collect { value ->
                _state.value = UiState(value)
            }
        }

        viewModelScope.launch {
            _state.update {
                it.copy(refreshing = true)

            }
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
    }

    fun onCreateDeckClick() {

    }

    fun onRefreshClicked() {
        _state.update {
            it.copy(refreshing = true)
        }

        viewModelScope.launch {
            delay(2.seconds)
            try {
                deckRepository.refreshAllUserDecks()
            } catch (e: Exception) {
                Logger.e(e.toString())
            }

            _state.update {
                it.copy(refreshing = false)
            }
        }
    }

    data class UiState internal constructor(
        val decks: List<Deck> = emptyList(),
        val refreshing: Boolean = false,
        val canCreateDecks: Boolean = false
    )
}



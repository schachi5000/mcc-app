package net.schacher.mcc.shared.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.repositories.AuthRepository
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.repositories.PackRepository
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.Spotlight
import net.schacher.mcc.shared.utils.debug
import kotlin.time.Duration.Companion.seconds

class MainViewModel(
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository,
    private val packRepository: PackRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())

    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Event>()

    val event = _event.asSharedFlow()

    init {
        Logger.debug { "Init MainViewModel" }
        this.viewModelScope.launch {
            delay(1.seconds)
            if (!cardRepository.hasCards()) {
                Logger.d { "No cards found in repo -> refreshing" }
                try {
                    cardRepository.refreshAllCards()
                    packRepository.refreshAllPacks()
                    _event.emit(Event.DatabaseSynced)
                } catch (e: Exception) {
                    Logger.e(e) { "Error refreshing cards" }
                    _event.emit(Event.CardsDatabaseSyncFailed(e))
                }
            }
        }

        this.viewModelScope.launch {
            authRepository.loginState.collect {
                _state.update {
                    it.copy(
                        mainScreen = Spotlight,
                        canShowMyDeckScreen = authRepository.isSignedIn()
                    )
                }
            }
        }
    }

    fun onLogoutClicked() {
        this.authRepository.logout()
    }

    sealed interface Event {
        data object DatabaseSynced : Event
        data class DeckCreated(val deckName: String) : Event
        data class CardsDatabaseSyncFailed(val exception: Exception) : Event
    }

    data class UiState internal constructor(
        val mainScreen: MainScreen = Spotlight,
        val canShowMyDeckScreen: Boolean = false,
    ) {
        sealed interface MainScreen {
            data object Spotlight : MainScreen
            data object MyDecks : MainScreen
            data object Collection : MainScreen
            data object Settings : MainScreen
        }
    }
}
package net.schacher.mcc.shared.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.schacher.mcc.shared.repositories.AuthRepository
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.MyDecks
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.Spotlight
import net.schacher.mcc.shared.utils.launchAndCollect

class MainViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        UiState(
            mainScreen = if (authRepository.isSignedInAsUser()) MyDecks else Spotlight,
        )
    )

    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Event>()

    val event = _event.asSharedFlow()

    init {
        this.viewModelScope.launchAndCollect(this.authRepository.loginState) {
            _state.update {
                it.copy(
                    mainScreen = if (authRepository.isSignedInAsUser()) MyDecks else Spotlight,
                )
            }
        }
    }

    fun onLogoutClicked() {
        this.authRepository.logout()
    }

    fun onTabSelected(selection: UiState.MainScreen) {
        _state.update { it.copy(mainScreen = selection) }
    }

    sealed interface Event {
        data object DatabaseSynced : Event
        data class DeckCreated(val deckName: String) : Event
        data class CardsDatabaseSyncFailed(val exception: Exception) : Event
    }

    data class UiState(
        val mainScreen: MainScreen,
    ) {
        sealed interface MainScreen {
            data object Spotlight : MainScreen
            data object MyDecks : MainScreen
            data object Collection : MainScreen
            data object Settings : MainScreen
        }
    }
}
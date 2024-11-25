package net.schacher.mcc.shared.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.datasource.database.SettingsDao
import net.schacher.mcc.shared.repositories.AuthRepository
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.PackRepository
import net.schacher.mcc.shared.screens.main.MainViewModel.UiState.MainScreen.Spotlight
import net.schacher.mcc.shared.utils.debug
import net.schacher.mcc.shared.utils.launchAndCollect

class MainViewModel(
    private val cardRepository: CardRepository,
    private val packRepository: PackRepository,
    private val authRepository: AuthRepository,
    private val settingsDao: SettingsDao
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())

    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Event>()

    val event = _event.asSharedFlow()

    init {
        Logger.debug { "Init MainViewModel" }
        this.viewModelScope.launch {
            if (!settingsDao.getBoolean("cards-synced", false)) {
                Logger.d { "No cards found in repo -> refreshing" }
                try {
                    packRepository.refreshAllPacks()
                    settingsDao.putBoolean("cards-synced", true)
                    _event.emit(Event.DatabaseSynced)
                } catch (e: Exception) {
                    Logger.e(e) { "Error refreshing cards" }
                    _event.emit(Event.CardsDatabaseSyncFailed(e))
                }
            }
        }

        this.viewModelScope.launchAndCollect(authRepository.loginState) {
            _state.update {
                it.copy(
                    mainScreen = Spotlight,
                    canShowMyDeckScreen = authRepository.isSignedInAsUser()
                )
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
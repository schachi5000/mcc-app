package net.schacher.mcc.shared.screens.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.AppLogger
import net.schacher.mcc.shared.repositories.AuthRepository
import net.schacher.mcc.shared.usecases.RefreshCardsInDatabaseUseCase

class AppViewModel(
    private val authRepository: AuthRepository,
    private val refreshCardsInDatabaseUseCase: RefreshCardsInDatabaseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(this.authRepository.loggedIn)

    val state = _state.asStateFlow()

    init {
        this.viewModelScope.launch {
            authRepository.loginState.collect {
                _state.value = it
            }
        }

        this.viewModelScope.launch() {
            try {
                refreshCardsInDatabaseUseCase()
            } catch (e: Exception) {
                AppLogger.e(e) { "Error refreshing cards" }
            }
        }
    }

    fun onGuestLoginClicked() {
        this.authRepository.loginAsGuest()
    }
}
package net.schacher.mcc.shared.screens.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.AppLogger
import net.schacher.mcc.shared.repositories.AuthRepository
import net.schacher.mcc.shared.repositories.PackRepository

class AppViewModel(
    private val packRepository: PackRepository,
    private val authRepository: AuthRepository
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
                packRepository.refreshAllPacks()
            } catch (e: Exception) {
                AppLogger.e(e) { "Error refreshing cards" }
            }
        }
    }

    fun onGuestLoginClicked() {
        this.authRepository.loginAsGuest()
    }
}
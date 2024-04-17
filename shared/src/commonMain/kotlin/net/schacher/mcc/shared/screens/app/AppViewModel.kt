package net.schacher.mcc.shared.screens.app

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.repositories.AuthRepository

class AppViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(this.authRepository.loggedIn)

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.loginState.collect {
                _state.value = it
            }
        }
    }

    fun onGuestLoginClicked() {
        this.authRepository.loginAsGuest()
    }
}
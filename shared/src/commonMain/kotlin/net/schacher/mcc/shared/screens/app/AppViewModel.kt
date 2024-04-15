package net.schacher.mcc.shared.screens.app

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.auth.AuthHandler

class AppViewModel(
    private val authHandler: AuthHandler
) : ViewModel() {

    private val _state = MutableStateFlow(this.authHandler.isLoggedIn())

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            authHandler.loginState.collect {
                _state.value = it
            }
        }
    }

    fun onGuestLoginClicked() {
        _state.value = true
    }
}
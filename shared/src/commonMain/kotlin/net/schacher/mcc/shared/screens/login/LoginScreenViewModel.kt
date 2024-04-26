package net.schacher.mcc.shared.screens.login

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.schacher.mcc.shared.repositories.AuthRepository

class LoginScreenViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(UiState.LOGIN_SELECTION)

    val state = _state.asStateFlow()

    private var infoSeen = false

    fun onLoginClicked() {
        _state.update {
            if (it == UiState.LOGIN_SELECTION && !infoSeen) {
                UiState.CONFIRMATION
            } else {
                UiState.ENTER_CREDENTIALS
            }
        }
    }

    fun onDismissInfoClicked() {
        this.infoSeen = true
        _state.update {
            UiState.ENTER_CREDENTIALS
        }
    }

    fun onDismissLoginClicked() {
        _state.update {
            UiState.LOGIN_SELECTION
        }
    }

    enum class UiState {
        LOGIN_SELECTION,
        CONFIRMATION,
        ENTER_CREDENTIALS,
    }
}
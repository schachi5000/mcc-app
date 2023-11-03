package net.schacher.mcc.shared.screens.main

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.repositories.CardRepository

class MainViewModel(private val cardRepository: CardRepository) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState(preparingApp = this.cardRepository.cards.isEmpty()))

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            if (cardRepository.cards.isEmpty()) {
                _state.update { it.copy(preparingApp = true) }
                cardRepository.refresh()
                _state.update { it.copy(preparingApp = false) }
            }
        }
    }

    fun onTabSelected(tabIndex: Int) {
        viewModelScope.launch {
            _state.update { it.copy(selectedTabIndex = tabIndex) }
        }
    }
}


data class MainUiState(
    val preparingApp: Boolean = true,
    val selectedTabIndex: Int = 1,
)
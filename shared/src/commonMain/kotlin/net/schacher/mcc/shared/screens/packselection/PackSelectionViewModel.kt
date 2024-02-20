package net.schacher.mcc.shared.screens.packselection

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.model.Pack
import net.schacher.mcc.shared.repositories.PackRepository

class PackSelectionViewModel(private val packRepository: PackRepository) : ViewModel() {

    private val _state = MutableStateFlow(
        UiState(this.packRepository.packs.value.map {
            UiState.Entry(it, false)
        })
    )

    val state = _state.asStateFlow()

    init {
        this.viewModelScope.launch {
            packRepository.packs.collect {
                refresh()
            }
        }
    }

    private fun refresh() {
        this.viewModelScope.launch {
            val packs = packRepository.packs.value
                .map { UiState.Entry(it, packRepository.hasPackInCollection(it.code)) }
                .sortedBy { it.pack.position }

            _state.update { UiState(packs) }
        }
    }

    fun onPackClicked(packCode: String) {
        this.viewModelScope.launch {
            if (packRepository.hasPackInCollection(packCode)) {
                packRepository.removePackFromCollection(packCode)
            } else {
                packRepository.addPackToCollection(packCode)
            }

            refresh()
        }
    }
}

data class UiState(val packs: List<Entry>) {
    data class Entry(val pack: Pack, val selected: Boolean)
}
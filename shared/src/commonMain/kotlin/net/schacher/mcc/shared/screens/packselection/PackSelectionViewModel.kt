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
        UiState(this.packRepository.allPacks.map {
            UiState.Entry(it, false)
        })
    )

    val state = _state.asStateFlow()

    init {
        this.viewModelScope.launch {
            packRepository.state.collect {
                refresh()
            }
        }
    }

    private fun refresh() {
        _state.update {
            UiState(packRepository.allPacks
                .map { UiState.Entry(it, packRepository.hasPackInCollection(it.code)) }
                .sortedBy { it.pack.position })
        }
    }

    fun onPackClicked(packCode: String) {
        if (this.packRepository.hasPackInCollection(packCode)) {
            this.packRepository.removePackFromCollection(packCode)
        } else {
            this.packRepository.addPackToCollection(packCode)
        }

        viewModelScope.launch { refresh() }
    }
}

data class UiState(val packs: List<Entry>) {
    data class Entry(val pack: Pack, val selected: Boolean)
}
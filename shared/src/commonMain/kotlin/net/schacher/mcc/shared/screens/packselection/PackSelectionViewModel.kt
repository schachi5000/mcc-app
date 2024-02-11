package net.schacher.mcc.shared.screens.packselection

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.model.Pack
import net.schacher.mcc.shared.repositories.PackRepository
import kotlin.random.Random

class PackSelectionViewModel(private val packRepository: PackRepository) : ViewModel() {

    private val _state = MutableStateFlow(
        UiState(this.packRepository.packs.map {
            UiState.Entry(it, Random.nextBoolean())
        })
    )

    val state = _state.asStateFlow()

    init {
        this.viewModelScope.launch {
            packRepository.state.collect {
                _state.emit(
                    UiState(it.map {
                        UiState.Entry(it, Random.nextBoolean())
                    })
                )
            }
        }
    }

    fun onPackClicked(packCode: String) {
        _state.update {
            val toMutableList = it.packs.toMutableList()
            val index = toMutableList.indexOfFirst { it.pack.code == packCode }
            val pack = toMutableList.removeAt(index)

            toMutableList.add(index, pack.copy(selected = !pack.selected))

            it.copy(
                packs = toMutableList
            )
        }
    }
}

data class UiState(val packs: List<Entry>) {
    data class Entry(val pack: Pack, val selected: Boolean)
}
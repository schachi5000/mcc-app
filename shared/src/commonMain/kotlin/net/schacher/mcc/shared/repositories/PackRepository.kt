package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Pack

class PackRepository(
    private val marvelCDbDataSource: MarvelCDbDataSource
) {
    private val _state = MutableStateFlow<List<Pack>>(emptyList())

    val state = _state.asStateFlow()

    val packs: List<Pack>
        get() = this.state.value

    init {
        MainScope().launch {
            marvelCDbDataSource.getAllPacks().let {
                _state.value = it
            }
        }
    }
}
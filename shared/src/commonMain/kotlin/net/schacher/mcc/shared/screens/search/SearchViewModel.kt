package net.schacher.mcc.shared.screens.search

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.screens.search.Filter.Type.AGGRESSION
import net.schacher.mcc.shared.screens.search.Filter.Type.JUSTICE
import net.schacher.mcc.shared.screens.search.Filter.Type.LEADERSHIP
import net.schacher.mcc.shared.screens.search.Filter.Type.OWNED
import net.schacher.mcc.shared.screens.search.Filter.Type.PROTECTION

class SearchViewModel(private val cardRepository: CardRepository) : ViewModel() {

    private val _state = MutableStateFlow(UiState())

    val state = _state.asStateFlow()

    fun onSearch(query: String?) {
        if (query.isNullOrEmpty()) {
            _state.update {
                UiState()
            }
            return
        }

        _state.update {
            it.copy(loading = true)
        }

        viewModelScope.launch(Dispatchers.Default) {
            val filteredCards = cardRepository.cards
                .filter { it.name.lowercase().contains(query.lowercase()) }
                .distinctBy { it.name }

            _state.update {
                it.copy(
                    result = filteredCards,
                    loading = false
                )
            }
        }
    }

    fun onFilterClicked(filter: Filter) {
        _state.update { uiState ->
            val newFilters = uiState.filters.map {
                if (it.type == filter.type) {
                    Filter(it.type, !it.active)
                } else {
                    it
                }
            }.toSet()

            uiState.copy(
                filters = newFilters
            )
        }
    }
}

data class UiState(
    val loading: Boolean = false,
    val result: List<Card> = emptyList(),
    val filters: Set<Filter> = setOf(
        Filter(OWNED, false),
        Filter(AGGRESSION, false),
        Filter(PROTECTION, false),
        Filter(JUSTICE, false),
        Filter(LEADERSHIP, false)
    )
) {
    val filtersEnabled: Boolean = filters.any { it.active } || filters.all { !it.active }
}

data class Filter(val type: Type, val active: Boolean) {
    enum class Type {
        OWNED,
        AGGRESSION,
        PROTECTION,
        JUSTICE,
        LEADERSHIP
    }
}


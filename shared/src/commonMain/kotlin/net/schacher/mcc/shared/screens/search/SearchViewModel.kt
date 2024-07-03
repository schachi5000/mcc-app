package net.schacher.mcc.shared.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Faction
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.PackRepository
import net.schacher.mcc.shared.screens.search.Filter.Type.AGGRESSION
import net.schacher.mcc.shared.screens.search.Filter.Type.BASIC
import net.schacher.mcc.shared.screens.search.Filter.Type.JUSTICE
import net.schacher.mcc.shared.screens.search.Filter.Type.LEADERSHIP
import net.schacher.mcc.shared.screens.search.Filter.Type.OWNED
import net.schacher.mcc.shared.screens.search.Filter.Type.PROTECTION
import net.schacher.mcc.shared.utils.distinctByName

class SearchViewModel(
    private val cardRepository: CardRepository,
    private val packRepository: PackRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow(UiState(result = cardRepository.cards.value.values.distinctByName()))

    val state = _state.asStateFlow()

    private var lastSearchJob: Job? = null

    fun onSearch(query: String?) {
        _state.update {
            it.copy(
                query = query,
                loading = query != null
            )
        }

        this.lastSearchJob?.cancel()
        this.lastSearchJob = this.viewModelScope.launch {
            _state.update {
                it.copy(
                    result = getFilteredCards(it.query, it.filters),
                    loading = false
                )
            }
        }
    }

    fun onFilterClicked(filter: Filter) {
        val value = _state.value

        val newFilters = value.filters.map {
            if (it.type == filter.type) {
                Filter(it.type, !it.active)
            } else {
                it
            }
        }.toSet()

        this.lastSearchJob?.cancel()
        this.lastSearchJob = this.viewModelScope.launch {
            _state.emit(
                value.copy(
                    filters = newFilters,
                    result = getFilteredCards(value.query, newFilters)
                )
            )
        }
    }

    private suspend fun getFilteredCards(query: String?, filters: Set<Filter> = emptySet()) =
        withContext(Dispatchers.Default) {
            val showOnlyOwned = filters.any { it.type == OWNED && it.active }
            cardRepository.cards.value.values
                .filter { card ->
                    filters.none { it.active } ||
                            filters.any { filter ->
                                when {
                                    filter.type == BASIC -> card.faction == Faction.BASIC
                                    filter.type == AGGRESSION -> card.aspect == Aspect.AGGRESSION
                                    filter.type == PROTECTION -> card.aspect == Aspect.PROTECTION
                                    filter.type == JUSTICE -> card.aspect == Aspect.JUSTICE
                                    filter.type == LEADERSHIP -> card.aspect == Aspect.LEADERSHIP
                                    showOnlyOwned -> packRepository.hasPackInCollection(card.packCode)
                                    else -> false
                                } && filter.active
                            }
                }
                .filter { card ->
                    query?.lowercase()?.let {
                        card.name.lowercase().contains(it) || card.packName.lowercase().contains(it)
                    } ?: true
                }
                .distinctByName()
        }
}

data class UiState(
    val loading: Boolean = false,
    val query: String? = null,
    val result: List<Card> = emptyList(),
    val filters: Set<Filter> = setOf(
        Filter(AGGRESSION, false),
        Filter(PROTECTION, false),
        Filter(JUSTICE, false),
        Filter(LEADERSHIP, false),
        Filter(BASIC, false),
        Filter(OWNED, false),
    )
)

data class Filter(val type: Type, val active: Boolean) {
    enum class Type {
        OWNED,
        AGGRESSION,
        PROTECTION,
        JUSTICE,
        LEADERSHIP,
        BASIC,
    }
}


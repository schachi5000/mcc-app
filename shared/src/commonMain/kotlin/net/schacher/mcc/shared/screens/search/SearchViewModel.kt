package net.schacher.mcc.shared.screens.search

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.screens.search.Filter.Type.AGGRESSION
import net.schacher.mcc.shared.screens.search.Filter.Type.JUSTICE
import net.schacher.mcc.shared.screens.search.Filter.Type.LEADERSHIP
import net.schacher.mcc.shared.screens.search.Filter.Type.OWNED
import net.schacher.mcc.shared.screens.search.Filter.Type.PROTECTION

class SearchViewModel(private val cardRepository: CardRepository) : ViewModel() {

    private val _state = MutableStateFlow(UiState(result = cardRepository.cards))

    val state = _state.asStateFlow()

    fun onSearch(query: String?) {
        if (query.isNullOrEmpty()) {
            _state.update {
                it.copy(
                    result = getFilteredCards(it.query, it.filters)
                )
            }
            return
        }

        _state.update {
            it.copy(
                query = query,
                loading = true
            )
        }

        _state.update {
            it.copy(
                result = getFilteredCards(it.query, it.filters),
                loading = false
            )
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

            val result = getFilteredCards(uiState.query, newFilters)

            uiState.copy(
                filters = newFilters,
                result = result
            )
        }
    }

    private fun getFilteredCards(query: String?, filters: Set<Filter>): List<Card> {
        return cardRepository.cards
            .filter { card ->
                filters.none { it.active } || filters.any { filter ->
                    when (filter.type) {
                        AGGRESSION -> card.aspect == Aspect.AGGRESSION
                        PROTECTION -> card.aspect == Aspect.PROTECTION
                        JUSTICE -> card.aspect == Aspect.JUSTICE
                        LEADERSHIP -> card.aspect == Aspect.JUSTICE
                        else -> false
                    } && filter.active
                }
            }
            .filter { card ->
                query?.let { query -> card.name.lowercase().contains(query.lowercase()) } ?: true
            }
            .distinctBy { it.name }
    }
}

data class UiState(
    val loading: Boolean = false,
    val query: String? = null,
    val result: List<Card> = emptyList(),
    val filters: Set<Filter> = setOf(
        Filter(OWNED, false),
        Filter(AGGRESSION, false),
        Filter(PROTECTION, false),
        Filter(JUSTICE, false),
        Filter(LEADERSHIP, false)
    )
)

data class Filter(val type: Type, val active: Boolean) {
    enum class Type {
        OWNED,
        AGGRESSION,
        PROTECTION,
        JUSTICE,
        LEADERSHIP
    }
}


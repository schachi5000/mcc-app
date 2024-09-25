package net.schacher.mcc.shared.screens.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
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
import net.schacher.mcc.shared.screens.search.Filter
import net.schacher.mcc.shared.screens.search.Filter.Type.AGGRESSION
import net.schacher.mcc.shared.screens.search.Filter.Type.BASIC
import net.schacher.mcc.shared.screens.search.Filter.Type.JUSTICE
import net.schacher.mcc.shared.screens.search.Filter.Type.LEADERSHIP
import net.schacher.mcc.shared.screens.search.Filter.Type.OWNED
import net.schacher.mcc.shared.screens.search.Filter.Type.PROTECTION
import net.schacher.mcc.shared.utils.distinctByName
import net.schacher.mcc.shared.utils.launchAndCollect

class CollectionViewModel(
    private val cardRepository: CardRepository,
    private val packRepository: PackRepository
) : ViewModel() {

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState(emptyList()))

    internal val state = _state.asStateFlow()

    init {
        this.viewModelScope.launchAndCollect(this.cardRepository.cards) {
            refresh()
        }

        this.viewModelScope.launchAndCollect(this.packRepository.packsInCollection) {
            refresh()
        }
    }

    private fun refresh() {
        this.viewModelScope.launch {
            val cards = getFilteredCards(_state.value.filters).toList()
            _state.update {
                it.copy(cardsInCollection = cards)
            }
        }
    }

    fun onApplyFilterClicked(filters: Set<Filter>) {
        _state.update {
            it.copy(filters = filters)
        }

        this.refresh()
    }

    private suspend fun getFilteredCards(filters: Set<Filter> = emptySet()) =
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
                .distinctByName()
        }
}

data class UiState internal constructor(
    val cardsInCollection: List<Card>,
    val filters: Set<Filter> = setOf(
        Filter(AGGRESSION, false),
        Filter(PROTECTION, false),
        Filter(JUSTICE, false),
        Filter(LEADERSHIP, false),
        Filter(BASIC, false),
        Filter(OWNED, false),
    )
)
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
import net.schacher.mcc.shared.model.CardType
import net.schacher.mcc.shared.model.Faction
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.PackRepository
import net.schacher.mcc.shared.screens.search.Filter
import net.schacher.mcc.shared.screens.search.Filter.Type.AGGRESSION
import net.schacher.mcc.shared.screens.search.Filter.Type.BASIC
import net.schacher.mcc.shared.screens.search.Filter.Type.HERO
import net.schacher.mcc.shared.screens.search.Filter.Type.JUSTICE
import net.schacher.mcc.shared.screens.search.Filter.Type.LEADERSHIP
import net.schacher.mcc.shared.screens.search.Filter.Type.OWNED
import net.schacher.mcc.shared.screens.search.Filter.Type.PROTECTION
import net.schacher.mcc.shared.utils.defaultSort
import net.schacher.mcc.shared.utils.distinctByName
import net.schacher.mcc.shared.utils.findAndRemove
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
            val cards = getFilteredCards(_state.value.filters
                .filter { it.active }
                .toMutableList()
            )
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

    private suspend fun getFilteredCards(filters: List<Filter>) =
        withContext(Dispatchers.Default) {
            val updatedFilter = filters.toMutableList()
            val showOnlyOwned = updatedFilter.findAndRemove { it.type == OWNED }?.active ?: false

            cardRepository.cards.value
                .filter { !showOnlyOwned || packRepository.hasPackInCollection(it.packCode) }
                .filter { card ->
                    updatedFilter.isEmpty() || updatedFilter.any {
                        when (it.type) {
                            BASIC -> card.faction == Faction.BASIC
                            AGGRESSION -> card.aspect == Aspect.AGGRESSION
                            PROTECTION -> card.aspect == Aspect.PROTECTION
                            JUSTICE -> card.aspect == Aspect.JUSTICE
                            LEADERSHIP -> card.aspect == Aspect.LEADERSHIP
                            HERO -> card.type == CardType.HERO
                            else -> false
                        }
                    }
                }
                .distinctByName()
                .defaultSort()
        }
}

data class UiState internal constructor(
    val cardsInCollection: List<Card>,
    val filters: Set<Filter> = setOf(
        Filter(AGGRESSION, false),
        Filter(PROTECTION, false),
        Filter(JUSTICE, false),
        Filter(LEADERSHIP, false),
        Filter(HERO, false),
        Filter(BASIC, false),
        Filter(OWNED, false),
    )
)
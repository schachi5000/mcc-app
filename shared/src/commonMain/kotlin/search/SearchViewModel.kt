package search

import data.CardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import model.Card

class SearchViewModel(private val cardRepository: CardRepository = CardRepository()) {

    private val _state = MutableStateFlow(SearchUiState())

    val state = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main)

    fun onSearch(query: String?) {
        if (query.isNullOrEmpty()) {
            _state.update {
                SearchUiState()
            }
            return
        }

        val filterCards = this.cardRepository.cards.value.filter {
            it.name.lowercase().contains(query.lowercase())
        }

        _state.update {
            it.copy(
                query = query,
                result = filterCards
            )
        }
    }
}

data class SearchUiState(
    val query: String? = null,
    val result: List<Card> = emptyList()
)
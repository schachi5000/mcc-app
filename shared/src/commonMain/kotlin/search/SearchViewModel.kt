package search

import data.CardRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.Card

class SearchViewModel(private val cardRepository: CardRepository) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())

    val state = _state.asStateFlow()

    fun onSearch(query: String?) {
        if (query.isNullOrEmpty()) {
            _state.update {
                SearchUiState()
            }
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            val filteredCards = cardRepository.cards.filter {
                it.name.lowercase().contains(query.lowercase())
            }

            _state.update {
                it.copy(
                    result = filteredCards
                )
            }
        }
    }
}

data class SearchUiState(
    val result: List<Card> = emptyList()
)
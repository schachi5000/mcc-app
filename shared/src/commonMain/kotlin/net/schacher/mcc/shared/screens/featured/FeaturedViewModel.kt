package net.schacher.mcc.shared.screens.featured

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.schacher.mcc.shared.datasource.http.KtorCardDataSource
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.CardRepository

class FeaturedViewModel(private val cardRepository: CardRepository) : ViewModel() {

    private val _state = MutableStateFlow(FeaturedUiState())

    val state = _state.asStateFlow()

    private val todayData: String
        get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).let {
            val dayOfMonth = it.dayOfMonth.let { day -> if (day < 10) "0$day" else day }
            "${it.year}-${it.monthNumber}-${dayOfMonth}"
        }

    init {
        this.onRefresh()
    }

    fun onRefresh() {
        _state.update {
            it.copy(
                refreshing = true
            )
        }

        viewModelScope.launch {
            val decks = KtorCardDataSource.getFeaturedDecksByDate(todayData) {
                cardRepository.getCard(it)
            }

            _state.update {
                it.copy(
                    decks = decks,
                    refreshing = false
                )
            }
        }
    }
}

data class FeaturedUiState(
    val decks: List<Deck> = emptyList(),
    val refreshing: Boolean = false
)
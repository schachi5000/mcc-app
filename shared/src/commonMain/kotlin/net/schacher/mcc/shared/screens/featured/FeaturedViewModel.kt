package net.schacher.mcc.shared.screens.featured

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import net.schacher.mcc.shared.datasource.http.KtorCardDataSource
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.CardRepository

class FeaturedViewModel(private val cardRepository: CardRepository) : ViewModel() {

    private val _state = MutableStateFlow(FeaturedUiState())

    val state = _state.asStateFlow()

    private val dates: List<String>
        get() {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return listOf(
                now.date.toDateString(),
                now.date.minus(1, kotlinx.datetime.DateTimeUnit.DAY).toDateString(),
                now.date.minus(2, kotlinx.datetime.DateTimeUnit.DAY).toDateString(),
            )
        }

    init {
        this.onRefresh()
    }

    fun onRefresh() {
        _state.update {
            it.copy(refreshing = true)
        }

        this.viewModelScope.launch {
            dates.forEach { date ->
                val result = KtorCardDataSource.getFeaturedDecksByDate(date) {
                    cardRepository.getCard(it)
                }

                val decks = result.getOrNull() ?: emptyList()
                _state.update {
                    it.copy(
                        decks = it.decks.toMutableMap()
                            .also { map -> map[date] = decks }
                    )
                }
            }

            _state.update {
                it.copy(refreshing = false)
            }
        }
    }
}

private fun LocalDate.toDateString(): String {
    val dayOfMonth = this.dayOfMonth.let { day -> if (day < 10) "0$day" else day }
    return "${this.year}-${this.monthNumber}-${dayOfMonth}"
}

data class FeaturedUiState(
    val decks: Map<String, List<Deck>> = emptyMap(),
    val refreshing: Boolean = false
)
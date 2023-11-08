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

    private val dates: List<LocalDate>
        get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).let {
            listOf(
                it.date,
                it.date.minus(1, kotlinx.datetime.DateTimeUnit.DAY),
                it.date.minus(2, kotlinx.datetime.DateTimeUnit.DAY),
            )
        }

    init {
        this.onRefresh()
    }

    fun onRefresh() {
        _state.update {
            it.copy(loading = true)
        }

        this.viewModelScope.launch {
            dates.forEach { date ->
                val result = KtorCardDataSource.getFeaturedDecksByDate(date) {
                    cardRepository.getAndUpdateCard(it)
                }

                val decks = result.getOrNull() ?: emptyList()
                _state.update {
                    it.copy(
                        decks = it.decks.toMutableMap()
                            .also { map -> map[date] = decks }
                            .filter { (_, entries) -> entries.isNotEmpty() },
                        loading = it.decks.entries.all { it.value.isEmpty() }
                    )
                }
            }

            _state.update {
                it.copy(loading = false)
            }
        }
    }
}

data class FeaturedUiState(
    val decks: Map<LocalDate, List<Deck>> = emptyMap(),
    val loading: Boolean = false
)
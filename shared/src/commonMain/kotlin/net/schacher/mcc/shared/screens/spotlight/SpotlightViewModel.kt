package net.schacher.mcc.shared.screens.spotlight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.SpotlightRepository

class SpotlightViewModel(
    private val spotlightRepository: SpotlightRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())

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
        if (this.state.value.loading) {
            return
        }

        _state.update {
            it.copy(
                decks = emptyMap(),
                loading = true
            )
        }

        this.viewModelScope.launch {
            val updatedDecks = mutableMapOf<LocalDate, List<Deck>>()
            dates.forEach { date ->
                async {
                    val spotlightDecks = spotlightRepository.getSpotlightDecks(date)

                    if (spotlightDecks.isNotEmpty()) {
                        updatedDecks[date] = spotlightDecks
                    }

                    _state.update {
                        it.copy(
                            decks = updatedDecks,
                            loading = updatedDecks.isNotEmpty()
                        )
                    }
                }.await()
            }

            _state.update {
                it.copy(loading = false)
            }
        }
    }

    data class UiState(
        val decks: Map<LocalDate, List<Deck>> = emptyMap(),
        val loading: Boolean = false
    )
}


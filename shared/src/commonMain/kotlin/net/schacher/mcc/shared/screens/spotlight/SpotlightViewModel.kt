package net.schacher.mcc.shared.screens.spotlight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
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
            spotlightRepository.getSpotlightDecks(dates)
                .onCompletion { _state.update { it.copy(loading = false) } }
                .collect { spotlight ->
                    _state.update {
                        val newMap = it.decks.toMutableMap().also {
                            it[spotlight.first] = spotlight.second
                        }

                        it.copy(
                            decks = newMap,
                            loading = !newMap.any { it.value.isNotEmpty() }
                        )
                    }
                }
        }
    }

    data class UiState(
        val decks: Map<LocalDate, List<Deck>> = emptyMap(),
        val loading: Boolean = false
    )
}


package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Deck

class SpotlightRepository(
    private val cardRepository: CardRepository,
    private val marvelCDbDataSource: MarvelCDbDataSource,
) {
    private val _state = MutableStateFlow<Map<LocalDate, List<Deck>>>(emptyMap())

    val state = _state.asStateFlow()

    fun getDeckById(deckId: Int): Deck? =
        this.state.value.values.flatten().find { it.id == deckId }

    suspend fun getSpotlightDecks(localDate: LocalDate): List<Deck> {
        val spotlight = this.marvelCDbDataSource.getSpotlightDecksByDate(localDate) {
            this.cardRepository.getCard(it)
        }.getOrNull()

        if (spotlight != null) {
            _state.update {
                it.toMutableMap().apply {
                    put(localDate, spotlight)
                }
            }
        }

        return spotlight ?: emptyList()
    }
}
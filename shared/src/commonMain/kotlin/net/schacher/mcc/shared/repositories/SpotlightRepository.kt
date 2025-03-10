package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    fun getSpotlightDecks(localDates: List<LocalDate>): Flow<Pair<LocalDate, List<Deck>>> =
        channelFlow {
            localDates.forEach { localDate ->
                launch(Dispatchers.Default) {
                    state.value[localDate]?.let {
                        send(Pair(localDate, it))
                    }

                    marvelCDbDataSource.getSpotlightDecksByDate(localDate) {
                        cardRepository.getCards(it)
                    }
                        .getOrNull()
                        ?.also { send(Pair(localDate, it)) }
                        ?.also { decks ->
                            _state.update {
                                it.toMutableMap().apply {
                                    put(localDate, decks)
                                }
                            }
                        }
                }
            }
        }
}
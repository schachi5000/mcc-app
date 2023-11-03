package net.schacher.mcc.shared.screens.featured

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.datasource.http.KtorCardDataSource
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.repositories.CardRepository

class FeaturedViewModel(private val cardRepository: CardRepository) : ViewModel() {

    private val _state = MutableStateFlow(FeaturedUiState())

    val state = _state.asStateFlow()

    init {
        Logger.d { "FeaturedViewModel init" }
        viewModelScope.launch {
            val decks = KtorCardDataSource.getFeaturedDecksByDate("2023-10-28") {
                cardRepository.getCard(it)
            }
            _state.emit(FeaturedUiState(decks))
        }
    }
}


data class FeaturedUiState(
    val decks: List<Deck> = emptyList()
)
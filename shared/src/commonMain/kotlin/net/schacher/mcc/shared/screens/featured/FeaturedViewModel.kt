package net.schacher.mcc.shared.screens.featured

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.datasource.KtorCardDataSource
import net.schacher.mcc.shared.model.Deck

class FeaturedViewModel : ViewModel() {

    private val _state = MutableStateFlow(FeaturedUiState())

    init {
        viewModelScope.launch {
            KtorCardDataSource.getFeaturedDecksByDate("2023-10-28")
        }
    }
}


data class FeaturedUiState(
    val featuredDecks: List<Deck> = emptyList()
)
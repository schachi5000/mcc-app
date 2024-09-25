package net.schacher.mcc.shared.screens.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.PackRepository
import net.schacher.mcc.shared.utils.launchAndCollect

class CollectionViewModel(
    private val cardRepository: CardRepository,
    private val packRepository: PackRepository
) : ViewModel() {

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState(emptyList()))

    internal val state = _state.asStateFlow()

    init {
        this.viewModelScope.launchAndCollect(this.cardRepository.cards) {
            refresh()
        }

        this.viewModelScope.launchAndCollect(this.packRepository.packsInCollection) {
            refresh()
        }
    }

    private fun refresh() {
        val cards = cardRepository.cards.value.values.toList().filter {
            this.packRepository.hasPackInCollection(it.packCode)
        }

        _state.value = UiState(cards)
    }
}

data class UiState internal constructor(val cardsInCollection: List<Card>)
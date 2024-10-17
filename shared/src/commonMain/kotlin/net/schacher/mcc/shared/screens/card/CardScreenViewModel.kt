package net.schacher.mcc.shared.screens.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType
import net.schacher.mcc.shared.repositories.AuthRepository
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository

class CardScreenViewModel(
    cardCode: String,
    cardRepository: CardRepository,
    private val authRepository: AuthRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _state: MutableStateFlow<UiState> = runBlocking {
        val card = cardRepository.getCard(cardCode)
        MutableStateFlow(
            UiState(
                card = card,
                canAddToDeck = canAddToDeck(card)
            )
        )
    }

    val state = _state.asStateFlow()

    fun onAddCardToDeck(it: Int, cardCode: String) {
        this.viewModelScope.launch {
            deckRepository.addCardToDeck(it, cardCode)
        }
    }

    private fun canAddToDeck(card: Card): Boolean =
        this.authRepository.isSignedInAsUser() && listOf<CardType>(
            CardType.ALLY,
            CardType.ATTACHMENT,
            CardType.RESOURCE,
            CardType.UPGRADE
        ).any {
            it == card.type
        }

    data class UiState internal constructor(
        val card: Card,
        val canAddToDeck: Boolean = false
    )
}
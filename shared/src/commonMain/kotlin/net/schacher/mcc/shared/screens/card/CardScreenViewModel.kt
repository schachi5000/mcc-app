package net.schacher.mcc.shared.screens.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.schacher.mcc.shared.AppLogger
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

    private companion object {
        const val TAG = "CardScreenViewModel"
    }

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
            try {
                deckRepository.addCardToDeck(it, cardCode)
            } catch (e: Exception) {
                AppLogger.e(TAG) { "Error adding card to deck: $e" }
            }
        }
    }

    private fun canAddToDeck(card: Card): Boolean =
        this.authRepository.isSignedInAsUser() && listOf(
            CardType.ALLY,
            CardType.ATTACHMENT,
            CardType.RESOURCE,
            CardType.UPGRADE,
            CardType.EVENT,
        ).any {
            it == card.type
        }

    data class UiState(
        val card: Card,
        val canAddToDeck: Boolean = false
    )
}
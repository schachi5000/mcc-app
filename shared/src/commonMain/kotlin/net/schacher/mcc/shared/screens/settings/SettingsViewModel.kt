package net.schacher.mcc.shared.screens.settings

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.database.SettingsDao
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.repositories.PackRepository

class SettingsViewModel(
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository,
    private val packRepository: PackRepository,
    private val settingsDao: SettingsDao
) : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsUiState(
            cardCount = cardRepository.cards.size,
            deckCount = deckRepository.state.value.size,
            packCount = packRepository.packs.size,
            settingsValues = settingsDao.getAllEntries()
        )
    )

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            deckRepository.state.collect { value ->
                _state.update { it.copy(deckCount = value.size) }
            }
        }
    }

    fun onWipeDatabaseClick() {
        if (this.state.value.syncInProgress) {
            return
        }

        viewModelScope.launch {
            Logger.d { "Wiping database..." }
            cardRepository.deleteAllCards()
            deckRepository.deleteAllDecks()
            Logger.d { "Wiping complete" }

            _state.update { it.copy(cardCount = 0, deckCount = 0) }
        }
    }

    fun onSyncClick() {
        _state.update { it.copy(syncInProgress = true) }

        viewModelScope.launch {
            cardRepository.refresh()

            _state.update {
                it.copy(
                    cardCount = cardRepository.cards.size,
                    deckCount = deckRepository.state.value.size,
                    syncInProgress = false
                )
            }
        }
    }

    fun addPublicDecksById(deckId: List<String>) {
        _state.update { it.copy(syncInProgress = false) }

        this.viewModelScope.launch {
            deckId.forEach {
                deckRepository.addDeckById(it.toInt())
                _state.update {
                    it.copy(
                        cardCount = cardRepository.cards.size,
                        deckCount = deckRepository.state.value.size,
                    )
                }
            }

            _state.update { it.copy(syncInProgress = false) }
        }
    }
}

data class SettingsUiState(
    val cardCount: Int,
    val deckCount: Int,
    val packCount: Int,
    val syncInProgress: Boolean = false,
    val settingsValues: List<Pair<String, Any>> = emptyList()
)
package net.schacher.mcc.shared.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schacher.mcc.shared.datasource.database.SettingsDao
import net.schacher.mcc.shared.platform.PlatformInfo
import net.schacher.mcc.shared.repositories.AuthRepository
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.DeckRepository
import net.schacher.mcc.shared.repositories.PackRepository
import net.schacher.mcc.shared.utils.launchAndCollect

class MoreViewModel(
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository,
    private val packRepository: PackRepository,
    private val authRepository: AuthRepository,
    private val settingsDao: SettingsDao,
    platformInfo: PlatformInfo
) : ViewModel() {

    private val _state = MutableStateFlow(
        UiState(
            cardCount = cardRepository.cards.value.size,
            cardsInCollection = 0,
            userDeckCount = deckRepository.decks.value.size,
            packCount = packRepository.packs.value.size,
            packsInCollectionCount = packRepository.packsInCollection.value.size,
            settingsValues = settingsDao.getAllEntries(),
            versionName = platformInfo.version,
            guestLogin = authRepository.isGuest()
        )
    )

    val state = _state.asStateFlow()

    init {
        viewModelScope.launchAndCollect(cardRepository.cards) { value ->
            _state.update { it.copy(cardCount = value.size) }
        }

        viewModelScope.launchAndCollect(deckRepository.decks) { value ->
            _state.update { it.copy(userDeckCount = value.size) }
        }

        viewModelScope.launchAndCollect(authRepository.loginState) {
            _state.update { it.copy(guestLogin = authRepository.isGuest()) }
        }

        viewModelScope.launchAndCollect(packRepository.packsInCollection) { value ->
            _state.update {
                it.copy(packsInCollectionCount = value.size,
                    cardsInCollection = cardRepository.cards.value.values
                        .filter { packRepository.hasCardInCollection(it) }
                        .size)
            }
        }

        viewModelScope.launchAndCollect(packRepository.packs) { value ->
            _state.update {
                it.copy(
                    packCount = value.size,
                    packsInCollectionCount = packRepository.packsInCollection.value.size
                )
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            _state.update {
                it.copy(
                    cardsInCollection = cardRepository.cards.value.values
                        .filter { packRepository.hasCardInCollection(it) }
                        .size
                )
            }
        }
    }

    fun onWipeDatabaseClick() {
        if (this.state.value.syncInProgress) {
            return
        }

        this.viewModelScope.launch {
            Logger.i { "Wiping database..." }
            cardRepository.deleteAllCardData()
            packRepository.deleteAllPackData()
            settingsDao.remove("cards-synced")
            Logger.i { "Wiping complete" }
        }
    }

    fun onSyncClick() {
        _state.update { it.copy(syncInProgress = true) }

        this.viewModelScope.launch {
            try {
                packRepository.refreshAllPacks()
            } catch (e: Exception) {
                Logger.e(e) { "Error refreshing packs" }
            }

            _state.update {
                it.copy(
                    cardCount = cardRepository.cards.value.size,
                    userDeckCount = deckRepository.decks.value.size,
                    packCount = packRepository.packs.value.size,
                    packsInCollectionCount = packRepository.packsInCollection.value.size,
                    syncInProgress = false
                )
            }
        }
    }

    data class UiState internal constructor(
        val cardCount: Int,
        val cardsInCollection: Int,
        val userDeckCount: Int,
        val packCount: Int,
        val packsInCollectionCount: Int,
        val syncInProgress: Boolean = false,
        val settingsValues: List<Pair<String, Any>> = emptyList(),
        val versionName: String,
        val guestLogin: Boolean
    )
}


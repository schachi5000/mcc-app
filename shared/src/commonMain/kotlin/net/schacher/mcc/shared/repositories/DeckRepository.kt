package net.schacher.mcc.shared.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.schacher.mcc.shared.datasource.database.DeckDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType.HERO
import net.schacher.mcc.shared.model.Deck
import kotlin.random.Random

class DeckRepository(
    private val cardRepository: CardRepository,
    private val spotlightRepository: SpotlightRepository,
    private val deckDatabaseDao: DeckDatabaseDao,
    private val marvelCDbDataSource: MarvelCDbDataSource,
    private val authRepository: AuthRepository
) {
    private val _decks = MutableStateFlow<List<Deck>>(emptyList())

    val decks = _decks.asStateFlow()

    init {
        MainScope().launch {
            authRepository.loginState.collect { loggedIn ->
                if (loggedIn) {
                    runCatching { refreshAllUserDecks() }
                } else {
                    _decks.emit(emptyList())
                }
            }
        }
    }

    private val randomDeckNumber: Int
        get() = Random.nextInt(Int.MAX_VALUE) * -1

    fun getDeckById(deckId: Int): Deck? =
        this.decks.value.find { it.id == deckId } ?: this.spotlightRepository.getDeckById(deckId)

    suspend fun createDeck(heroCard: Card, label: String? = null, aspect: Aspect? = null) {
        if (heroCard.type != HERO) {
            throw Exception("Hero card must be of type HERO - $heroCard")
        }

        val deckLabel = label ?: "${heroCard.name} ${aspect?.name ?: ""}"
        val defaultCards =
            heroCard.setCode?.let { cardRepository.getCardsBySetCode(it) } ?: emptyList()

        val deck = Deck(randomDeckNumber, deckLabel, heroCard, aspect, defaultCards)

        this.deckDatabaseDao.addDeck(deck)
        _decks.update { deckDatabaseDao.getDecks() }
    }

    suspend fun removeDeck(deckId: Int) {
        this.deckDatabaseDao.removeDeck(deckId)

        _decks.update {
            it.toMutableList().also {
                it.removeAll { it.id == deckId }
            }
        }
    }

    suspend fun addCardToDeck(deckId: Int, cardCode: String) {
        val card = this.cardRepository.getCard(cardCode)
        val deck = this.decks.value.find { it.id == deckId }
        checkNotNull(deck) { "Deck with id $deckId not found" }

        val newDeck = deck.copy(
            cards = deck.cards + card
        )

        this.deckDatabaseDao.addDeck(newDeck)

        _decks.update { deckDatabaseDao.getDecks() }
    }

    suspend fun refreshAllUserDecks() {
        val decks = marvelCDbDataSource.getUserDecks {
            this.cardRepository.getCard(it)
        }

//        decks.forEach {
//            this.deckDatabaseDao.addDeck(it)
//        }

        _decks.emit(decks)
    }

    suspend fun addDeckById(deckId: Int) {
        val deck = marvelCDbDataSource.getUserDeckById(deckId) {
            this.cardRepository.getCard(it)
        }

        this.deckDatabaseDao.addDeck(deck)
        _decks.emit(deckDatabaseDao.getDecks())
    }

    suspend fun deleteAllDeckData() = withContext(Dispatchers.IO) {
        deckDatabaseDao.wipeDeckTable()
        _decks.emit(deckDatabaseDao.getDecks())
    }
}
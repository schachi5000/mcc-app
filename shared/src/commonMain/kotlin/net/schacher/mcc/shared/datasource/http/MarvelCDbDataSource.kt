package net.schacher.mcc.shared.datasource.http

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.model.Pack

interface MarvelCDbDataSource {

    suspend fun getAllPackCodes(): Result<List<String>>

    fun getPacks(packCodes: List<String>): Flow<Pack>

    fun getAllPacks(): Flow<Pack>

    suspend fun getCardsInPack(packCode: String): Result<List<Card>>

    suspend fun getCard(cardCode: String): Result<Card>

    suspend fun getSpotlightDecksByDate(
        date: LocalDate,
        cardProvider: suspend (String) -> Card
    ): Result<List<Deck>>

    suspend fun getUserDecks(cardProvider: suspend (String) -> Card): Result<List<Deck>>

    suspend fun getUserDeckById(
        deckId: Int,
        cardProvider: suspend (String) -> Card
    ): Result<Deck>

    suspend fun createDeck(heroCardCode: String, deckName: String? = null): Result<Int>

    suspend fun updateDeck(deck: Deck, cardProvider: suspend (String) -> Card): Result<Deck>

    suspend fun deleteDeck(deckId: Int): Result<Unit>
}
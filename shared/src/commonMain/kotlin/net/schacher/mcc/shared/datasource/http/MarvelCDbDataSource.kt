package net.schacher.mcc.shared.datasource.http

import kotlinx.datetime.LocalDate
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.model.Pack

interface MarvelCDbDataSource {

    suspend fun getAllPacks(): List<Pack>

    suspend fun getCardPack(packCode: String): List<Card>

    suspend fun getAllCards(): List<Card>

    suspend fun getCard(cardCode: String): Card

    suspend fun getSpotlightDecksByDate(
        date: LocalDate,
        cardProvider: suspend (String) -> Card
    ): Result<List<Deck>>

    suspend fun getUserDecks(cardProvider: suspend (String) -> Card): List<Deck>

    suspend fun getUserDeckById(deckId: Int, cardProvider: suspend (String) -> Card): Deck
}
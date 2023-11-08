package net.schacher.mcc.shared.datasource.http

import kotlinx.datetime.LocalDate
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck

interface MarvelCDbDataSource {

    suspend fun getCardPack(packCode: String): List<Card>
    suspend fun getAllCards(): List<Card>
    suspend fun getCard(cardCode: String): Card
    suspend fun getFeaturedDecksByDate(date: LocalDate, cardProvider: suspend (String) -> Card?): Result<List<Deck>>
    suspend fun getPublicDeckById(deckId: Int, cardProvider: (String) -> Card?): Deck
}
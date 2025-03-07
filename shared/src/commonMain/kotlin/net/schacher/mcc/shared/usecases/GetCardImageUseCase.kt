package net.schacher.mcc.shared.usecases

import net.schacher.mcc.shared.datasource.database.CardDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource

class GetCardImageUseCase(
    private val marvelCDbDataSource: MarvelCDbDataSource,
    private val cardDatabaseDao: CardDatabaseDao
) {
    suspend operator fun invoke(cardCode: String): ByteArray =
        this.cardDatabaseDao.getCardImage(cardCode) ?: run {
            val image = marvelCDbDataSource.getCardImage(cardCode).getOrThrow()
            cardDatabaseDao.addCardImage(cardCode, image)
            image
        }
}
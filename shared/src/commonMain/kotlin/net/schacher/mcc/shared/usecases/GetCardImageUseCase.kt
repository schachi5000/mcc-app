package net.schacher.mcc.shared.usecases

import net.schacher.mcc.shared.datasource.database.CardDatabaseDao
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource

class GetCardImageUseCase(
    private val marvelCDbDataSource: MarvelCDbDataSource,
    private val cardDatabaseDao: CardDatabaseDao
) {
    suspend operator fun invoke(cardCode: String): ByteArray {
        val databaseImage = this.cardDatabaseDao.getCardImage(cardCode)
        if (databaseImage != null) {
            return databaseImage
        }

        return this.marvelCDbDataSource.getCardImage(cardCode).also {
            this.cardDatabaseDao.addCardImage(cardCode, it)
        }
    }
}
package net.schacher.mcc.shared.usecases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import net.schacher.mcc.shared.AppLogger
import net.schacher.mcc.shared.datasource.http.MarvelCDbDataSource
import net.schacher.mcc.shared.model.Pack
import net.schacher.mcc.shared.repositories.CardRepository
import net.schacher.mcc.shared.repositories.PackRepository
import kotlin.time.measureTime

class RefreshCardsInDatabaseUseCase(
    private val cardRepository: CardRepository,
    private val packRepository: PackRepository,
    private val marvelCDbDataSource: MarvelCDbDataSource,
) {
    private companion object {
        const val TAG = "RefreshCardsInDatabaseUseCase"
    }

    private val _refreshing = MutableStateFlow(false)

    val refreshing = _refreshing.asStateFlow()

    suspend operator fun invoke() {
        AppLogger.d(TAG) { "Refreshing all packs" }

        try {
            this._refreshing.value = true

            val packCodes = this.marvelCDbDataSource.getAllPackCodes().getOrNull() ?: emptyList()
            val unknownPackCodes = packCodes.filter { !this.packRepository.hasPack(it) }
            if (unknownPackCodes.isEmpty()) {
                AppLogger.d(TAG) { "No new packs to refresh" }
                return
            }

            val packs = this.marvelCDbDataSource.getPacks(unknownPackCodes).getOrThrow()
            withContext(Dispatchers.Default) {
                packs.map {
                    async {
                        val time = measureTime {
                            AppLogger.d(TAG) { "Refreshing pack [${it.name}]" }
                            refreshPack(it)

                        }
                        AppLogger.d(TAG) { "Done...Refreshing pack [${it.name}]. Took $time" }
                    }
                }.awaitAll()
            }
        } finally {
            this._refreshing.value = false
        }
    }

    private suspend fun refreshPack(pack: Pack) {
        try {
            this.packRepository.addPacks(listOf(pack))
            val cardsInPack = this.marvelCDbDataSource.getCardsInPack(pack.code).getOrThrow()
            this.cardRepository.addCards(cardsInPack)
        } catch (e: Exception) {
            AppLogger.e { "Error adding pack [${pack.name}] to database: ${e.message}" }
        }
    }
}
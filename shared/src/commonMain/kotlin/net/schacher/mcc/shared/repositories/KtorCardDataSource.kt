package net.schacher.mcc.shared.repositories

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.serialization.json.Json
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.model.Pack
import kotlin.coroutines.coroutineContext

// TODO convert to class and hide behind interface
object KtorCardDataSource {

    private const val BASE_URL = "https://de.marvelcdb.com"

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    Logger.d { message }
                }
            }
            level = LogLevel.INFO
        }
    }

    suspend fun getAllCardPacks() = httpClient
        .get("$BASE_URL/api/public/packs")
        .body<List<Pack>>()

    suspend fun getCardPack(packCode: String) = httpClient
        .get("$BASE_URL/api/public/cards/$packCode")
        .body<List<Card>>()

    suspend fun getAllCards() = getAllCardPacks()
        .map {
            CoroutineScope(coroutineContext).async {
                Logger.d { "Starting download of: ${it.name}" }
                val result = getCardPack(it.code)
                Logger.d { "finished download of: ${it.name}" }
                result
            }
        }
        .awaitAll()
        .flatten()

    suspend fun getCard(cardCode: String) = httpClient
        .get("$BASE_URL/api/public/card/$cardCode")
        .body<Card>()

    suspend fun getFeaturedDecksByDate(date: String): List<Deck> {
        return emptyList()
    }
}
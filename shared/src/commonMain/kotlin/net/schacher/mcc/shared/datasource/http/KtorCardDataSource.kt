package net.schacher.mcc.shared.datasource.http

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
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.model.Pack
import kotlin.coroutines.coroutineContext

// TODO convert to class and hide behind interface
object KtorCardDataSource {

    private const val BASE_URL = "https://de.marvelcdb.com/api/public"


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
        .get("$BASE_URL/packs")
        .body<List<Pack>>()

    suspend fun getCardPack(packCode: String) = httpClient
        .get("$BASE_URL/cards/$packCode")
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
        .get("$BASE_URL/card/$cardCode")
        .body<Card>()

    suspend fun getFeaturedDecksByDate(date: String, cardProvider: (String) -> Card?) = httpClient
        .get("$BASE_URL/decklists/by_date/$date")
        .body<List<DeckDto>>()
        .map {
            Deck(
                id = it.id,
                name = it.name,
                heroCard = getCard(it.investigator_code!!),
                aspect = it.aspect,
                cards = it.slots.entries.map { entry ->
                    List(entry.value) { cardProvider(entry.key) }
                }.flatten().filterNotNull()
            )
        }

    suspend fun getPublicDeckById(deckId: Int, cardProvider: (String) -> Card?) = httpClient
        .get("$BASE_URL/deck/$deckId")
        .body<DeckDto>()
        .let {
            Deck(
                id = it.id,
                name = it.name,
                heroCard = getCard(it.investigator_code!!),
                aspect = it.aspect,
                cards = it.slots.entries.map { entry ->
                    List(entry.value) { cardProvider(entry.key) }
                }.flatten().filterNotNull()
            )
        }
}

private const val LEADERSHIP = "leadership"
private const val JUSTICE = "justice"
private const val AGGRESSION = "aggression"
private const val PROTECTION = "protection"

private val DeckDto.aspect: Aspect?
    get() = when {
        this.meta?.contains(JUSTICE) == true -> Aspect.JUSTICE
        this.meta?.contains(LEADERSHIP) == true -> Aspect.LEADERSHIP
        this.meta?.contains(AGGRESSION) == true -> Aspect.AGGRESSION
        this.meta?.contains(PROTECTION) == true -> Aspect.PROTECTION
        else -> null
    }
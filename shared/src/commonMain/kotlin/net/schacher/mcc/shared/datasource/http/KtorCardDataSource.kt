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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.Deck
import kotlin.coroutines.coroutineContext

// TODO convert to class and hide behind interface
object KtorCardDataSource {

    private const val BASE_URL = "https://de.marvelcdb.com/api/public"


    @OptIn(ExperimentalSerializationApi::class)
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
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
        .body<List<PackDto>>()

    suspend fun getCardPack(packCode: String) = httpClient
        .get("$BASE_URL/cards/$packCode")
        .body<List<CardDto>>()
        .map { it.toCard() }

    suspend fun getAllCards() = this.getAllCardPacks()
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
        .body<CardDto>()
        .toCard()

    suspend fun getFeaturedDecksByDate(date: String, cardProvider: (String) -> Card?) =
        runCatching {
            this.httpClient
                .get("$BASE_URL/decklists/by_date/$date")
                .body<List<DeckDto>>()
                .map {
                    Deck(
                        id = it.id,
                        name = it.name,
                        heroCard = getCard(it.investigator_code!!),
                        aspect = it.meta?.parseAspect(),
                        cards = it.slots.entries.map { entry ->
                            List(entry.value) { cardProvider(entry.key) }
                        }.flatten().filterNotNull()
                    )
                }
        }

    suspend fun getPublicDeckById(deckId: Int, cardProvider: (String) -> Card?) = httpClient
        .get("$BASE_URL/deck/$deckId")
        .body<DeckDto>()
        .let {
            val heroCard = getCard(it.investigator_code!!)

            Deck(
                id = it.id,
                name = it.name,
                heroCard = heroCard,
                aspect = it.meta?.parseAspect(),
                cards = it.slots.entries
                    .map { entry ->
                        List(entry.value) { cardProvider(entry.key) }
                    }
                    .flatten()
                    .filterNotNull()
                    .toMutableList()
                    .also { it.add(0, heroCard) }
            )
        }
}

private const val LEADERSHIP = "leadership"
private const val JUSTICE = "justice"
private const val AGGRESSION = "aggression"
private const val PROTECTION = "protection"

private fun String.parseAspect(): Aspect? = when {
    this.contains(JUSTICE) -> Aspect.JUSTICE
    this.contains(LEADERSHIP) -> Aspect.LEADERSHIP
    this.contains(AGGRESSION) -> Aspect.AGGRESSION
    this.contains(PROTECTION) -> Aspect.PROTECTION
    else -> null
}

private fun CardDto.toCard() = Card(
    code = this.code,
    position = this.position,
    type = this.type_code,
    name = this.name,
    packCode = this.pack_code,
    aspect = this.faction_code.parseAspect(),
)
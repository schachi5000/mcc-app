package net.schacher.mcc.shared.datasource.http

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.toUpperCasePreservingASCIIRules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.schacher.mcc.shared.datasource.http.dto.CardDto
import net.schacher.mcc.shared.datasource.http.dto.DeckDto
import net.schacher.mcc.shared.datasource.http.dto.DeckUpdateResponseDto
import net.schacher.mcc.shared.datasource.http.dto.PackDto
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.Card
import net.schacher.mcc.shared.model.CardType
import net.schacher.mcc.shared.model.CardType.ALLY
import net.schacher.mcc.shared.model.CardType.ATTACHMENT
import net.schacher.mcc.shared.model.CardType.ENVIRONMENT
import net.schacher.mcc.shared.model.CardType.EVENT
import net.schacher.mcc.shared.model.CardType.HERO
import net.schacher.mcc.shared.model.CardType.MAIN_SCHEME
import net.schacher.mcc.shared.model.CardType.MINION
import net.schacher.mcc.shared.model.CardType.OBLIGATION
import net.schacher.mcc.shared.model.CardType.PLAYER_SIDE_SCHEME
import net.schacher.mcc.shared.model.CardType.RESOURCE
import net.schacher.mcc.shared.model.CardType.SIDE_SCHEME
import net.schacher.mcc.shared.model.CardType.SUPPORT
import net.schacher.mcc.shared.model.CardType.TREACHERY
import net.schacher.mcc.shared.model.CardType.UPGRADE
import net.schacher.mcc.shared.model.CardType.VILLAIN
import net.schacher.mcc.shared.model.Deck
import net.schacher.mcc.shared.model.Faction
import net.schacher.mcc.shared.model.Pack
import net.schacher.mcc.shared.repositories.AuthRepository
import kotlin.coroutines.CoroutineContext

class KtorMarvelCDbDataSource(
    private val authRepository: AuthRepository,
    private val serviceUrl: String
) : MarvelCDbDataSource {

    private companion object {
        const val TAG = "KtorMarvelCDbDataSource"
        const val AUTHORIZATION = "Authorization"
        const val MAX_RETRY_DELAY_MS = 10000L
        const val MAX_RETRIES = 2
    }

    private val authHeader: String
        get() = "Bearer ${this.authRepository.accessToken?.token ?: throw IllegalStateException("No access token available")}"

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
                    Logger.d(TAG) { message }
                }
            }
            level = LogLevel.INFO
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = MAX_RETRIES)
            exponentialDelay(maxDelayMs = MAX_RETRY_DELAY_MS)
        }
    }

    override suspend fun getAllPacks() = withContextSafe(Dispatchers.IO) {
        httpClient.get("$serviceUrl/packs")
            .body<List<PackDto>>()
            .map {
                async(Dispatchers.Default) {
                    Logger.d { "Processing Pack: ${it.name}" }
                    val cards = getCardsInPack(it.code).getOrThrow()

                    Pack(
                        code = it.code,
                        name = it.name,
                        cards = cards,
                        id = it.id,
                        position = it.position
                    ).also {
                        Logger.d { "Processing done: ${it.name}" }
                    }
                }
            }
            .awaitAll()
    }

    override suspend fun getCardsInPack(packCode: String) =
        withContextSafe(Dispatchers.IO) {
            httpClient.get("$serviceUrl/packs/$packCode")
                .body<List<CardDto>>()
                .map {
                    val card = it.toCard()
                    val linkedCard = it.linkedCard?.toCard()?.copy(
                        linkedCard = card
                    )

                    card.copy(
                        linkedCard = linkedCard
                    )
                }
        }

    override suspend fun getCard(cardCode: String) = withContextSafe(Dispatchers.IO) {
        httpClient.get("$serviceUrl/cards/$cardCode")
            .body<CardDto>()
            .let {
                val card = it.toCard()
                val linkedCard = it.linkedCard?.toCard()?.copy(
                    linkedCard = card
                )

                card.copy(
                    linkedCard = linkedCard
                )
            }
    }

    override suspend fun getSpotlightDecksByDate(
        date: LocalDate, cardProvider: suspend (String) -> Card
    ) = withContextSafe(Dispatchers.IO) {
        httpClient.get("$serviceUrl/decks/spotlight") {
            parameter("date", date.toDateString())
        }
            .body<List<DeckDto>>()
            .map { it.toDeck(cardProvider) }
    }.also {
        it.exceptionOrNull()?.let {
            Logger.e(it) { "Failed to get spotlight decks" }
        }
    }

    override suspend fun getUserDecks(cardProvider: suspend (String) -> Card) =
        withContextSafe(Dispatchers.IO) {
            httpClient
                .get("$serviceUrl/decks") {
                    headers { append("Authorization", authHeader) }
                }
                .body<List<DeckDto>>()
                .map { it.toDeck(cardProvider) }
                .sortedBy { it.name }
        }.also {
            it.exceptionOrNull()?.let {
                Logger.e(it) { "Failed to get user decks" }
            }
        }

    override suspend fun getUserDeckById(
        deckId: Int,
        cardProvider: suspend (String) -> Card
    ) = runCatching {
        this.getUserDeckDtoById(deckId).getOrThrow().toDeck(cardProvider)
    }

    private suspend fun getUserDeckDtoById(deckId: Int) = withContextSafe(Dispatchers.IO) {
        httpClient.get("$serviceUrl/decks/$deckId") {
            headers { append(AUTHORIZATION, authHeader) }
        }.body<DeckDto>()
    }

    override suspend fun updateDeck(deck: Deck, cardProvider: suspend (String) -> Card) =
        withContextSafe(Dispatchers.IO) {
            val slots = deck.cards.toMutableList()
                .also { it.removeAll { it.code == deck.hero.code } }
                .groupingBy { it.code }
                .eachCount()
                .let { Json.encodeToString(it) }

            val response = httpClient.put("$serviceUrl/decks/${deck.id}") {
                headers { append(AUTHORIZATION, authHeader) }
                parameter("slots", slots)
            }.body<DeckUpdateResponseDto>()

            if (response.success) {
                getUserDeckById(deck.id) { cardProvider(it) }.getOrThrow()
            } else {
                throw Exception("Failed to update deck: ${response.msg?.toString()}")
            }
        }
}

private fun LocalDate.toDateString(): String {
    val dayOfMonth = this.dayOfMonth.let { if (it < 10) "0$it" else it }
    val month = this.monthNumber.let { if (it < 10) "0$it" else it }

    return "${this.year}-${month}-${dayOfMonth}"
}

private const val LEADERSHIP = "leadership"
private const val JUSTICE = "justice"
private const val AGGRESSION = "aggression"
private const val PROTECTION = "protection"

private fun String.parseAspect(): Aspect? = when (this) {
    JUSTICE -> Aspect.JUSTICE
    LEADERSHIP -> Aspect.LEADERSHIP
    AGGRESSION -> Aspect.AGGRESSION
    PROTECTION -> Aspect.PROTECTION
    else -> null
}

private fun CardDto.toCard() = Card(
    code = this.code,
    position = this.position,
    type = this.typeCode.toCardType(),
    cost = this.cost,
    name = this.name.trim(),
    setCode = this.cardSetCode,
    setName = this.cardSetName,
    packCode = this.packCode,
    packName = this.packName,
    text = this.text?.replace("\n", "\n\n"),
    boostText = this.boostText.cleanUp(),
    attackText = this.attackText.cleanUp(),
    quote = this.flavor.cleanUp(),
    aspect = this.factionCode.parseAspect(),
    traits = this.traits?.takeIf { it.isNotEmpty() },
    faction = Faction.valueOf(this.factionCode.toUpperCasePreservingASCIIRules()),
)

private fun String?.cleanUp(): String? =
    this?.replace("\n ", "\n")?.trim()?.takeIf { it.isNotEmpty() }

private fun String?.toCardType(): CardType? = when (this) {
    "hero" -> HERO
    "ally" -> ALLY
    "event" -> EVENT
    "support" -> SUPPORT
    "upgrade" -> UPGRADE
    "resource" -> RESOURCE
    "villain" -> VILLAIN
    "main_scheme" -> MAIN_SCHEME
    "side_scheme" -> SIDE_SCHEME
    "player_side_scheme" -> PLAYER_SIDE_SCHEME
    "attachment" -> ATTACHMENT
    "minion" -> MINION
    "treachery" -> TREACHERY
    "environment" -> ENVIRONMENT
    "obligation" -> OBLIGATION
    else -> null
}

private suspend fun DeckDto.toDeck(cardProvider: suspend (String) -> Card): Deck {
    val heroCard = cardProvider(this.heroCode!!)

    return Deck(id = this.id,
        name = this.name,
        hero = heroCard,
        aspect = this.aspect?.parseAspect(),
        cards = (this.slots ?: emptyMap()).entries
            .map { entry ->
                List(entry.value) {
                    cardProvider(entry.key)
                }
            }
            .flatten()
            .toMutableList()
            .also { it.add(0, heroCard) },
        problem = this.problem,
        version = this.version
    )
}

private suspend fun <T> withContextSafe(
    context: CoroutineContext,
    block: suspend CoroutineScope.() -> T
): Result<T> = withContext(context) {
    runCatching<T> {
        block()
    }
}
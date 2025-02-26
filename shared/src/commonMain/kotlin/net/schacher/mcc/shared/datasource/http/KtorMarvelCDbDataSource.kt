package net.schacher.mcc.shared.datasource.http

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.toUpperCasePreservingASCIIRules
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.schacher.mcc.shared.AppLogger
import net.schacher.mcc.shared.datasource.http.dto.CardDto
import net.schacher.mcc.shared.datasource.http.dto.CreateDeckRequestDto
import net.schacher.mcc.shared.datasource.http.dto.CreateDeckResponseDto
import net.schacher.mcc.shared.datasource.http.dto.DeckDto
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
    private val httpClient: HttpClient,
    private val authRepository: AuthRepository,
    private val serviceUrl: String
) : MarvelCDbDataSource {

    private companion object {
        const val AUTHORIZATION = "Authorization"
    }

    private val authHeader: String
        get() = this.authRepository.accessToken?.token?.let { "Bearer $it" }
            ?: throw IllegalStateException("No access token available")

    override suspend fun getAllPackCodes(): Result<List<String>> = withContextSafe {
        httpClient.get("$serviceUrl/packs")
            .body<List<PackDto>>()
            .map { it.code }
    }

    override fun getPacks(packCodes: List<String>) = channelFlow {
        httpClient.get("$serviceUrl/packs").body<List<PackDto>>()
            .filter { packCodes.contains(it.code) }
            .forEach { packDto ->
                launch {
                    AppLogger.d { "Processing Pack: ${packDto.name}" }
                    val cards = getCardsInPack(packDto.code).getOrThrow()

                    send(
                        Pack(
                            id = packDto.id,
                            code = packDto.code,
                            name = packDto.name,
                            cards = cards,
                            cardCodes = cards.map { it.code },
                            position = packDto.position
                        )
                    )
                }
                AppLogger.d { "Processing done: ${packDto.name}" }
            }
    }

    override fun getAllPacks(): Flow<Pack> = channelFlow {
        httpClient.get("$serviceUrl/packs").body<List<PackDto>>()
            .forEach { packDto ->
                launch {
                    AppLogger.d { "Processing Pack: ${packDto.name}" }
                    val cards = getCardsInPack(packDto.code).getOrThrow()

                    send(
                        Pack(
                            id = packDto.id,
                            code = packDto.code,
                            name = packDto.name,
                            cards = cards,
                            cardCodes = cards.map { it.code },
                            position = packDto.position
                        )
                    )
                }
                AppLogger.d { "Processing done: ${packDto.name}" }
            }
    }

    override suspend fun getCardsInPack(packCode: String) = withContextSafe {
        httpClient.get("$serviceUrl/packs/$packCode")
            .body<List<CardDto>>()
            .map {
                val card = it.toCard()
                var linkedCard = it.linkedCard?.toCard()

                linkedCard = linkedCard?.copy(
                    linkedCard = card
                )

                card.copy(
                    linkedCard = linkedCard
                )
            }
    }

    override suspend fun getCard(cardCode: String) = withContextSafe {
        httpClient.get("$serviceUrl/cards/$cardCode")
            .body<CardDto>()
            .let {
                val card = it.toCard()
                var linkedCard = it.linkedCard?.toCard()

                linkedCard = linkedCard?.copy(
                    linkedCard = card
                )

                card.copy(
                    linkedCard = linkedCard
                )
            }
    }

    override suspend fun getSpotlightDecksByDate(
        date: LocalDate,
        cardProvider: suspend (String) -> Card
    ) = withContextSafe {
        httpClient.get("$serviceUrl/decks/spotlight") {
            parameter("date", date.toDateString())
        }
            .body<List<DeckDto>>()
            .map { it.toDeck(cardProvider) }
    }.also {
        it.exceptionOrNull()?.let {
            AppLogger.e(it) { "Failed to get spotlight decks" }
        }
    }

    override suspend fun getUserDecks(cardProvider: suspend (String) -> Card) =
        withContextSafe {
            httpClient
                .get("$serviceUrl/decks") {
                    headers { append("Authorization", authHeader) }
                }
                .body<List<DeckDto>>()
                .map { it.toDeck(cardProvider) }
                .sortedBy { it.name }
        }.also {
            it.exceptionOrNull()?.let {
                AppLogger.e(it) { "Failed to get user decks" }
            }
        }

    override suspend fun getUserDeckById(
        deckId: Int,
        cardProvider: suspend (String) -> Card
    ) = runCatching {
        this.getUserDeckDtoById(deckId).getOrThrow().toDeck(cardProvider)
    }

    private suspend fun getUserDeckDtoById(deckId: Int) = withContextSafe {
        httpClient.get("$serviceUrl/decks/$deckId") {
            headers { append(AUTHORIZATION, authHeader) }
        }.body<DeckDto>()
    }

    override suspend fun createDeck(heroCardCode: String, deckName: String?): Result<Int> =
        withContextSafe {
            httpClient.post("$serviceUrl/decks") {
                headers { append(AUTHORIZATION, authHeader) }
                contentType(ContentType.Application.Json)
                setBody(CreateDeckRequestDto(heroCardCode, deckName))
            }
                .body<CreateDeckResponseDto>()
                .deckId
        }

    override suspend fun updateDeck(deck: Deck, cardProvider: suspend (String) -> Card) =
        withContextSafe {
            val slots = deck.cards.toMutableList()
                .also { it.removeAll { it.code == deck.hero.code } }
                .groupingBy { it.code }
                .eachCount()
                .let { Json.encodeToString(it) }

            httpClient.put("$serviceUrl/decks/${deck.id}") {
                headers { append(AUTHORIZATION, authHeader) }
                parameter("slots", slots)
            }

            getUserDeckById(deck.id) { cardProvider(it) }.getOrThrow()
        }

    override suspend fun deleteDeck(deckId: Int): Result<Unit> = withContextSafe {
        httpClient.delete("$serviceUrl/decks/${deckId}") {
            headers { append(AUTHORIZATION, authHeader) }
        }
    }
}

class ServiceException(val status: Int, message: String) : IOException("[${status}] $message")

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
    type = this.type.toCardType(),
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
    primaryColor = this.primaryColor,
    secondaryColor = this.secondaryColor,
    linkedCardCode = this.linkedCard?.code,
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
        version = this.version,
        description = this.description
    )
}

private suspend fun <T> withContextSafe(
    context: CoroutineContext = Dispatchers.IO,
    block: suspend CoroutineScope.() -> T
): Result<T> = withContext(context) {
    runCatching<T> {
        block()
    }
}
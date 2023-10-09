package provider

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import model.Card

object CardDataSource {

    private const val BASE_URL = "https://de.marvelcdb.com"

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getCardPack(packCode: String) = httpClient
        .get("$BASE_URL/api/public/cards/$packCode")
        .body<List<Card>>()

    suspend fun getCard(cardCode: String) = httpClient
        .get("$BASE_URL/api/public/card/$cardCode")
        .body<Card>()
}
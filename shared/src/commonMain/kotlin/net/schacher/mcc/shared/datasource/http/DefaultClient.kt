package net.schacher.mcc.shared.datasource.http

import androidx.compose.ui.text.intl.Locale
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.schacher.mcc.shared.AppLogger
import net.schacher.mcc.shared.datasource.http.dto.ErrorResponseDto

private const val TAG = "HttpClient"
private const val REQUEST_TIMEOUT_MS = 5000L
private const val MAX_RETRY_DELAY_MS = 5000L
private const val MAX_RETRIES = 2

val DefaultHttpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        })
    }
    install(ContentEncoding) {
        gzip()
        deflate()
    }
    install(DefaultRequest) {
        headers {
            append("Accept-Language", Locale.current.language)
        }
    }
    install(Logging) {
        logger = object : io.ktor.client.plugins.logging.Logger {
            override fun log(message: String) {
                AppLogger.d(TAG) { message }
            }
        }
        level = LogLevel.INFO
    }
    install(HttpTimeout) {
        requestTimeoutMillis = REQUEST_TIMEOUT_MS
    }
    install(HttpRequestRetry) {
        exponentialDelay(maxDelayMs = MAX_RETRY_DELAY_MS)
        retryOnException(
            maxRetries = MAX_RETRIES,
            retryOnTimeout = true
        )
    }

    HttpResponseValidator {
        validateResponse { response ->
            if (response.status != HttpStatusCode.OK) {
                val errorMessage = runCatching {
                    response.body<ErrorResponseDto>()
                }.getOrNull()?.message ?: response.bodyAsText()

                throw ServiceException(
                    status = response.status.value,
                    message = errorMessage
                )
            }
        }
    }
}
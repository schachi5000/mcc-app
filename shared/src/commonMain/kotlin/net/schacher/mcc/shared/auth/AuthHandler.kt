package net.schacher.mcc.shared.auth

import co.touchlab.kermit.Logger
import io.ktor.http.Url
import net.schacher.mcc.shared.time.Time
import net.schacher.mcc.shared.utils.debug
import kotlin.time.Duration.Companion.seconds

object AuthHandler {

    var accessToken: AccessToken? = null
        private set


    fun handleCallbackUrl(callbackUrl: String) {
        Logger.debug { "Handling callback url: $callbackUrl" }
        this.accessToken = TokenUtils.parseData(callbackUrl)
    }
}


object TokenUtils {

    fun parseData(callbackUrl: String): AccessToken = Url(callbackUrl).let {
        AccessToken(
            token = it.parameters["access_token"]
                ?: throw IllegalArgumentException("No access token found"),
            expiresAt = it.parameters["expires_in"]?.toLongOrNull()
                ?.let { Time.currentTimeMillis + it.seconds.inWholeMilliseconds }
                ?: throw IllegalArgumentException("No expiration time found")
        )
    }
}

data class AccessToken(
    val token: String,
    val expiresAt: Long
)
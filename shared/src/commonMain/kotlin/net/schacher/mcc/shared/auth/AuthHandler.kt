package net.schacher.mcc.shared.auth

import co.touchlab.kermit.Logger
import io.ktor.http.Url
import net.schacher.mcc.shared.datasource.database.SettingsDao
import net.schacher.mcc.shared.time.Time
import net.schacher.mcc.shared.utils.debug
import kotlin.time.Duration.Companion.seconds

class NewAuthHandler(private val settingsDao: SettingsDao)

object AuthHandler {

    const val APP_SCHEME = "mccapp"

    val loggedIn: Boolean
        get() = this.accessToken != null &&
                (this.accessToken?.expiresAt ?: 0) > Time.currentTimeMillis

    val authHeader: String
        get() = "Bearer ${this.accessToken?.token ?: throw IllegalStateException("No access token available")}"

    var accessToken: AccessToken? = null
        private set(value) {
            field = value
            Logger.d { "Access token set to $value" }
        }


    fun handleCallbackUrl(callbackUrl: String): Boolean {
        val fixedCallbackUrl = callbackUrl.replace("#", "?")
        Logger.debug { "Handling callback url: $fixedCallbackUrl" }
        this.accessToken = try {
            TokenUtils.parseData(fixedCallbackUrl)
        } catch (e: Exception) {
            Logger.e(throwable = e) { "Error parsing access token from $fixedCallbackUrl" }
            null
        }

        return this.accessToken != null
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
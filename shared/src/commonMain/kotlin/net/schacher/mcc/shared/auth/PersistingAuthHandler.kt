package net.schacher.mcc.shared.auth

import co.touchlab.kermit.Logger
import io.ktor.http.Url
import net.schacher.mcc.shared.datasource.database.SettingsDao
import net.schacher.mcc.shared.time.Time
import net.schacher.mcc.shared.utils.debug
import kotlin.time.Duration.Companion.seconds

class PersistingAuthHandler(private val settingsDao: SettingsDao) : AuthHandler {
    companion object {
        const val APP_SCHEME = "mccapp"
        private const val ACCESS_TOKEN = "access_token"
        private const val EXPIRES_AT = "access_token_expires_at"
    }

    override val authHeader: String
        get() = "Bearer ${TokenHolder.accessToken?.token ?: throw IllegalStateException("No access token available")}"

    override val loggedIn: Boolean
        get() = TokenHolder.accessToken != null &&
                (TokenHolder.accessToken?.expiresAt ?: 0) > Time.currentTimeMillis

    init {
        this.restoreAccessToken()
    }

    private fun restoreAccessToken() {
        val token = this.settingsDao.getString(ACCESS_TOKEN)
        val expiresAt = this.settingsDao.getString(EXPIRES_AT)?.toLongOrNull()

        if (token != null && expiresAt != null) {
            TokenHolder.accessToken = AccessToken(token, expiresAt)
        }
    }

    override fun handleCallbackUrl(callbackUrl: String): Boolean {
        val fixedCallbackUrl = callbackUrl.replace("#", "?")
        Logger.debug { "Handling callback url: $fixedCallbackUrl" }

        TokenHolder.accessToken = try {
            this.parseData(fixedCallbackUrl)
        } catch (e: Exception) {
            Logger.e(throwable = e) { "Error parsing access token from $fixedCallbackUrl" }
            null
        }?.also {
            this.storeAccessToken(it)
        }

        return TokenHolder.accessToken != null
    }

    private fun parseData(callbackUrl: String): AccessToken = Url(callbackUrl).let {
        AccessToken(
            token = it.parameters["access_token"]
                ?: throw IllegalArgumentException("No access token found"),
            expiresAt = it.parameters["expires_in"]?.toLongOrNull()
                ?.let { Time.currentTimeMillis + it.seconds.inWholeMilliseconds }
                ?: throw IllegalArgumentException("No expiration time found")
        )
    }

    private fun storeAccessToken(accessToken: AccessToken) {
        this.settingsDao.putString(ACCESS_TOKEN, accessToken.token)
        this.settingsDao.putString(EXPIRES_AT, accessToken.expiresAt.toString())

    }
}

object TokenHolder {
    var accessToken: AccessToken? = null
        internal set(value) {
            field = value
            Logger.d { "Access token set to $value" }
        }
}

data class AccessToken(
    val token: String,
    val expiresAt: Long
) {
    val remainingTime: Long
        get() = this.expiresAt - Time.currentTimeMillis

    override fun toString(): String {
        return "AccessToken(token='$token', expiresAt=$expiresAt, remainingTime=$remainingTime)"
    }
}
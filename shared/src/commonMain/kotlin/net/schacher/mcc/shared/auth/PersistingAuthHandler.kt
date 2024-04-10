package net.schacher.mcc.shared.auth

import co.touchlab.kermit.Logger
import io.ktor.http.Url
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private var accessToken: AccessToken? = null
        set(value) {
            field = value
            Logger.debug { "Access token set to $value" }

            this._loginState.value = this.isLoggedIn()
        }

    private val _loginState = MutableStateFlow(this.isLoggedIn())

    override val loginState: StateFlow<Boolean> = _loginState.asStateFlow()

    init {
        this.restoreAccessToken()
    }

    override fun isLoggedIn(): Boolean = this.accessToken != null &&
            (this.accessToken?.expiresAt ?: 0) > Time.currentTimeMillis

    override val authHeader: String
        get() = "Bearer ${this.accessToken?.token ?: throw IllegalStateException("No access token available")}"

    private fun restoreAccessToken() {
        val token = this.settingsDao.getString(ACCESS_TOKEN)
        val expiresAt = this.settingsDao.getString(EXPIRES_AT)?.toLongOrNull()

        if (token != null && expiresAt != null) {
            this.accessToken = AccessToken(token, expiresAt)
        }
    }

    override fun handleCallbackUrl(callbackUrl: String): Boolean {
        val fixedCallbackUrl = callbackUrl.replace("#", "?")
        Logger.debug { "Handling callback url: $fixedCallbackUrl" }

        this.accessToken = try {
            this.parseData(fixedCallbackUrl)
        } catch (e: Exception) {
            Logger.e(throwable = e) { "Error parsing access token from $fixedCallbackUrl" }
            null
        }?.also {
            this.storeAccessToken(it)
        }

        return this.accessToken != null
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

    override fun logout() {
        this.accessToken = null

        this.settingsDao.remove(ACCESS_TOKEN)
        this.settingsDao.remove(EXPIRES_AT)
    }


    private fun storeAccessToken(accessToken: AccessToken) {
        this.settingsDao.putString(ACCESS_TOKEN, accessToken.token)
        this.settingsDao.putString(EXPIRES_AT, accessToken.expiresAt.toString())
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
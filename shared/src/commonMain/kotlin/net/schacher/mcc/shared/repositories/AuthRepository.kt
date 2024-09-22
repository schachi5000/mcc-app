package net.schacher.mcc.shared.repositories

import co.touchlab.kermit.Logger
import io.ktor.http.Url
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.schacher.mcc.shared.datasource.database.SettingsDao
import net.schacher.mcc.shared.time.Time
import net.schacher.mcc.shared.utils.debug
import kotlin.time.Duration.Companion.seconds

class AuthRepository(private val settingsDao: SettingsDao) {
    companion object {
        const val APP_SCHEME = "mccapp"
        private const val ACCESS_TOKEN = "access_token"
        private const val EXPIRES_AT = "access_token_expires_at"
    }

    private val _loginState = MutableStateFlow(this.loggedIn)

    val loginState: StateFlow<Boolean> = _loginState.asStateFlow()

    init {
        this.restoreAccessToken()
    }

    private var userType: UserType? = null
        set(value) {
            field = value
            Logger.debug { "UserType set to $value" }

            this._loginState.value = this.loggedIn
        }

    val accessToken: AccessToken?
        get() = when (val userType = this.userType) {
            is UserType.AuthorizedUser -> userType.accessToken
            else -> null
        }

    val loggedIn: Boolean
        get() = this.isLoggedIn(this.userType)

    private fun isLoggedIn(userType: UserType? = this.userType) = when (userType) {
        is UserType.AuthorizedUser -> this.isAccessTokenValid(userType.accessToken)
        is UserType.Guest -> true
        else -> false
    }

    private fun restoreAccessToken() {
        val token = this.settingsDao.getString(ACCESS_TOKEN)
        val expiresAt = this.settingsDao.getString(EXPIRES_AT)?.toLongOrNull()

        if (token != null && expiresAt != null) {
            val accessToken = AccessToken(token, expiresAt)
            if (isAccessTokenValid(accessToken)) {
                this.userType = UserType.AuthorizedUser(accessToken)
            } else {
                this.logout()
            }
        }
    }

    fun loginAsGuest() {
        this.logout()
        this.userType = UserType.Guest
    }

    fun handleCallbackUrl(callbackUrl: String): Boolean {
        val fixedCallbackUrl = callbackUrl.replace("#", "?")
        Logger.debug { "Handling callback url: $fixedCallbackUrl" }

        val accessToken = try {
            this.parseData(fixedCallbackUrl)
        } catch (e: Exception) {
            Logger.e(throwable = e) { "Error parsing access token from $fixedCallbackUrl" }
            null
        }

        return if (accessToken != null) {
            this.userType = UserType.AuthorizedUser(accessToken)
            this.storeAccessToken(accessToken)
            true
        } else {
            false
        }
    }

    private fun parseData(callbackUrl: String): AccessToken = Url(callbackUrl).let {
        AccessToken(token = it.parameters["access_token"]
            ?: throw IllegalArgumentException("No access token found"),
            expiresAt = it.parameters["expires_in"]?.toLongOrNull()
                ?.let { Time.currentTimeMillis + it.seconds.inWholeMilliseconds }
                ?: throw IllegalArgumentException("No expiration time found"))
    }

    fun logout() {
        this.userType = null

        this.settingsDao.remove(ACCESS_TOKEN)
        this.settingsDao.remove(EXPIRES_AT)
    }


    private fun storeAccessToken(accessToken: AccessToken) {
        this.settingsDao.putString(ACCESS_TOKEN, accessToken.token)
        this.settingsDao.putString(EXPIRES_AT, accessToken.expiresAt.toString())
    }

    private fun isAccessTokenValid(accessToken: AccessToken?): Boolean =
        (accessToken?.expiresAt ?: 0) > Time.currentTimeMillis

    fun isGuest(): Boolean = this.userType is UserType.Guest

    fun isSignedIn(): Boolean = this.userType is UserType.AuthorizedUser
}


internal sealed class UserType {
    data class AuthorizedUser(val accessToken: AccessToken) : UserType()

    data object Guest : UserType()
}

data class AccessToken internal constructor(val token: String, val expiresAt: Long)
package net.schacher.mcc.shared.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthHandler {

    val loginState: StateFlow<Boolean>

    val authHeader: String

    fun isLoggedIn(): Boolean

    fun handleCallbackUrl(callbackUrl: String): Boolean
}

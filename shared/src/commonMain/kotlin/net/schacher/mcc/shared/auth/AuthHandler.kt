package net.schacher.mcc.shared.auth

interface AuthHandler {

    val loggedIn: Boolean

    val authHeader: String

    fun handleCallbackUrl(callbackUrl: String): Boolean
}

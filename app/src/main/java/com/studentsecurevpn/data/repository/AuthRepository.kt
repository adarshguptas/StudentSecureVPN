package com.studentsecurevpn.data.repository

import com.studentsecurevpn.data.remote.VpnApiService
import com.studentsecurevpn.data.remote.model.LoginRequest
import com.studentsecurevpn.util.SecurePreferenceManager

/** Handles login and guest mode. Tokens are stored encrypted. */
class AuthRepository(
    private val api: VpnApiService,
    private val securePrefs: SecurePreferenceManager
) {
    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        require(email.isNotBlank()) { "Email is required" }
        require(password.isNotBlank()) { "Password is required" }
        val response = api.login(LoginRequest(email, password))
        securePrefs.saveToken(response.token)
        securePrefs.setGuestMode(false)
    }

    fun continueAsGuest() {
        securePrefs.clearToken()
        securePrefs.setGuestMode(true)
    }

    fun isSignedInOrGuest(): Boolean = securePrefs.isGuestMode() || securePrefs.token() != null
}

package com.studentsecurevpn.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Encrypted storage for sensitive preferences such as auth tokens and selected server id.
 * Falls back to private app storage only if the Android Keystore is unavailable on an emulator/device.
 */
class SecurePreferenceManager(context: Context) {
    private val prefs: SharedPreferences = runCatching {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }.getOrElse {
        context.getSharedPreferences("secure_prefs_fallback", Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) = prefs.edit().putString(KEY_TOKEN, token).apply()
    fun token(): String? = prefs.getString(KEY_TOKEN, null)
    fun clearToken() = prefs.edit().remove(KEY_TOKEN).apply()
    fun setGuestMode(enabled: Boolean) = prefs.edit().putBoolean(KEY_GUEST, enabled).apply()
    fun isGuestMode(): Boolean = prefs.getBoolean(KEY_GUEST, false)
    fun setSelectedServerId(id: String) = prefs.edit().putString(KEY_SERVER, id).apply()
    fun selectedServerId(): String? = prefs.getString(KEY_SERVER, null)

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_GUEST = "guest_mode"
        private const val KEY_SERVER = "selected_server_id"
    }
}

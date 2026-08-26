package com.juko.app.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

/**
 * TokenManager provides simple storage for access & refresh tokens.
 *
 * NOTE: We include a production-friendly Settings-backed implementation (SettingsTokenManager)
 * but wire an InMemoryTokenManager by default as a mock for Phase 0.
 */
interface TokenManager {
    suspend fun saveAccessToken(token: String)
    suspend fun saveRefreshToken(token: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clear()
}

class SettingsTokenManager(private val settings: Settings) : TokenManager {
    private val KEY_ACCESS = "access_token"
    private val KEY_REFRESH = "refresh_token"

    override suspend fun saveAccessToken(token: String) {
        settings[KEY_ACCESS] = token
    }

    override suspend fun saveRefreshToken(token: String) {
        settings[KEY_REFRESH] = token
    }

    override suspend fun getAccessToken(): String? = settings.getStringOrNull(KEY_ACCESS)

    override suspend fun getRefreshToken(): String? = settings.getStringOrNull(KEY_REFRESH)

    override suspend fun clear() {
        settings.remove(KEY_ACCESS)
        settings.remove(KEY_REFRESH)
    }
}

/**
 * Simple in-memory mock implementation used for development phases until real storage is set up.
 */
class InMemoryTokenManager : TokenManager {
    private var access: String? = null
    private var refresh: String? = null

    override suspend fun saveAccessToken(token: String) { access = token }
    override suspend fun saveRefreshToken(token: String) { refresh = token }
    override suspend fun getAccessToken(): String? = access
    override suspend fun getRefreshToken(): String? = refresh
    override suspend fun clear() { access = null; refresh = null }
}

// Extension helper for russhwolf Settings
private fun Settings.getStringOrNull(key: String): String? = try { this.getString(key) } catch (t: Throwable) { null }

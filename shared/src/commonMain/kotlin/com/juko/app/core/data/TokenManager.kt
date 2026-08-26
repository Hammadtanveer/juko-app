package com.juko.app.core.data

/**
 * TokenManager (shared mock) used for DI in androidApp until real storage is wired.
 */
interface TokenManager {
    suspend fun saveAccessToken(token: String)
    suspend fun saveRefreshToken(token: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clear()
}

class InMemoryTokenManager : TokenManager {
    private var access: String? = null
    private var refresh: String? = null

    override suspend fun saveAccessToken(token: String) { access = token }
    override suspend fun saveRefreshToken(token: String) { refresh = token }
    override suspend fun getAccessToken(): String? = access
    override suspend fun getRefreshToken(): String? = refresh
    override suspend fun clear() { access = null; refresh = null }
}

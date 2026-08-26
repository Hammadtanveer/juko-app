package com.juko.app.di

import com.juko.app.core.data.InMemoryTokenManager
import com.juko.app.core.data.TokenManager
import com.juko.app.core.network.ApiClient
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

/**
 * Koin application module wiring TokenManager and ApiClient as singletons.
 * Uses in-memory mock TokenManager and a mock ApiClient by default.
 */
val appModule = module {
    single<TokenManager> { InMemoryTokenManager() }

    single {
        // tokenProvider must be sync; use runBlocking to call the suspend TokenManager API
        ApiClient.create(isMock = true, tokenProvider = {
            runBlocking { get<TokenManager>().getAccessToken() }
        })
    }
}

package com.juko.app.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.serialization.json.Json

/**
 * Lightweight ApiClient factory.
 * - Base URL: https://staging-api.juko.app/api/v1
 * - JSON Content Negotiation
 * - Logging
 * - Auth (Bearer) plugin wired to a token provider
 *
 * For now: default isMock = true so no real network calls are performed.
 */
object ApiClient {
    const val BASE_URL = "https://staging-api.juko.app/api/v1"

    fun create(
        isMock: Boolean = true,
        tokenProvider: (() -> String?)? = null
    ): HttpClient {
        val json = Json { ignoreUnknownKeys = true }

        if (isMock) {
            val engine = MockEngine { request ->
                // Basic mock response: return empty JSON by default.
                respond("{}")
            }

            return HttpClient(engine) {
                install(ContentNegotiation) { json(json) }
                install(Logging) { level = LogLevel.NONE }
                // Auth plugin available but no-op for mock
                install(Auth) {
                    bearer {
                        loadTokens { tokenProvider?.invoke()?.let { BearerTokens(it, "") } }
                    }
                }
            }
        }

        // Real client (will still be safe, but left here for future use)
        return HttpClient {
            install(ContentNegotiation) { json(json) }
            install(Logging) { logger = Logger.SIMPLE; level = LogLevel.INFO }
            install(Auth) {
                bearer {
                    loadTokens { tokenProvider?.invoke()?.let { BearerTokens(it, "") } }
                }
            }
            defaultRequest {
                header("Content-Type", "application/json")
            }
        }
    }
}

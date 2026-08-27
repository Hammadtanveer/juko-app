package com.juko.app.di

import com.juko.app.core.data.InMemoryTokenManager
import com.juko.app.core.data.TokenManager
import com.juko.app.feature.auth.di.authModule
import com.juko.app.feature.postride.di.postRideModule
import org.koin.dsl.module

val coreModule = module {
    single<TokenManager> { InMemoryTokenManager() }
    // ApiClient will be added to DI after Ktor is added to shared module
}

val appModule = module {
    includes(coreModule, authModule, postRideModule)
}

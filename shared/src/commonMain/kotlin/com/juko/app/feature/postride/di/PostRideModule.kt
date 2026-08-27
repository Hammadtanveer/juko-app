package com.juko.app.feature.postride.di

import com.juko.app.feature.postride.data.repository.FakePostRideRepository
import com.juko.app.feature.postride.domain.repository.PostRideRepository
import com.juko.app.feature.postride.domain.usecase.PublishRideUseCase
import com.juko.app.feature.postride.presentation.PostRideViewModel
import org.koin.dsl.module

val postRideModule = module {
    single<PostRideRepository> { FakePostRideRepository() }
    factory { PublishRideUseCase(get()) }
    factory { PostRideViewModel(get()) }
}

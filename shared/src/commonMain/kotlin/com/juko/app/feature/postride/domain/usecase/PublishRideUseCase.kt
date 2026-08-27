package com.juko.app.feature.postride.domain.usecase

import com.juko.app.feature.postride.domain.model.RideOffer
import com.juko.app.feature.postride.domain.repository.PostRideRepository

class PublishRideUseCase(
    private val repository: PostRideRepository
) {
    suspend operator fun invoke(offer: RideOffer): Result<RideOffer> {
        return repository.publishRide(offer)
    }
}

package com.juko.app.feature.postride.domain.repository

import com.juko.app.feature.postride.domain.model.RideOffer

interface PostRideRepository {
    suspend fun publishRide(offer: RideOffer): Result<RideOffer>
}

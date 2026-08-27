package com.juko.app.feature.postride.data.repository

import com.juko.app.feature.postride.domain.model.RideOffer
import com.juko.app.feature.postride.domain.repository.PostRideRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakePostRideRepository : PostRideRepository {
    
    private val _offers = MutableStateFlow<List<RideOffer>>(emptyList())
    val offers: StateFlow<List<RideOffer>> = _offers.asStateFlow()

    override suspend fun publishRide(offer: RideOffer): Result<RideOffer> {
        delay(1500) // Simulate network delay
        val newOffer = offer.copy(id = "ride_${(1000..9999).random()}")
        _offers.update { it + newOffer }
        return Result.success(newOffer)
    }
}

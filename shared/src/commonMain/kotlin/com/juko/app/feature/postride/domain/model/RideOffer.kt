package com.juko.app.feature.postride.domain.model

data class RideOffer(
    val id: String? = null,
    val driverId: String,
    val origin: String,
    val destination: String,
    val departureTime: Long,
    val pricePerSeat: Int,
    val availableSeats: Int,
    val vehicleId: String,
    val luggageAllowed: Boolean = true,
    val acAvailable: Boolean = true,
    val maxTwoBackSeat: Boolean = true,
    val smokingAllowed: Boolean = false,
    val petsAllowed: Boolean = false,
    val notes: String = ""
)

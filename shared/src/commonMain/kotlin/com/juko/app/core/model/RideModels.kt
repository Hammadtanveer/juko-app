package com.juko.app.core.model

/**
 * Domain model for a customer's confirmed booking as a passenger.
 */
data class CustomerBookingModel(
    val id: String,
    val rideId: String = "",
    val origin: String,
    val boardingStop: String,
    val destination: String,
    val departureDate: String,
    val departureTime: String,
    val driverName: String,
    val driverAvatar: String?,
    val vehicleName: String,
    val seatsBooked: Int,
    val totalFare: Int,
    val boardingPin: String = "4829",
    val status: String = "Confirmed"
)

/**
 * Passenger entry on a driver's published ride, categorized by boarding stop.
 */
data class PassengerEntry(
    val id: String = "",
    val name: String,
    val boardingStop: String,
    val seats: Int,
    val totalFare: Int = 0,
    val phone: String = "",
    val avatar: String? = null
)

/**
 * Domain model for a ride published by a driver/host.
 */
data class PublishedRideModel(
    val id: String,
    val origin: String,
    val destination: String,
    val intermediateStops: List<String> = emptyList(),
    val dateTime: String,
    val departureTime: String = "08:00 AM",
    val departureDate: String = "Today",
    val status: String = "Active", // "Active", "Draft", "Completed", "Cancelled"
    val filledSeats: Int = 0,
    val totalSeats: Int = 4,
    val pricePerSeat: Int = 450,
    val vehicleName: String = "Swift Dzire",
    val driverName: String = "Alex Rivera",
    val driverAvatar: String? = null,
    val driverRating: Double = 4.8,
    val routeLocations: List<RouteLocation> = emptyList(),
    val destinationPrices: Map<String, Int> = emptyMap(),
    val passengersList: List<PassengerEntry> = emptyList(),
    val isDriverVerified: Boolean = true,
    val departureDistanceKm: Double = 0.5,
    val arrivalDistanceKm: Double = 0.5,
    val durationMinutes: Int = 120
)

/**
 * Domain model for an incoming booking request from a passenger.
 */
data class BookingRequestModel(
    val id: String,
    val rideId: String = "",
    val passengerName: String,
    val rating: Double,
    val ridesCount: Int,
    val seatsRequested: Int,
    val distanceAway: String,
    val pickupStation: String,
    val routeStops: List<String>,
    val date: String,
    val timeSlot: String,
    val isWholeCar: Boolean,
    val seatPreference: String?,
    val toEarn: Int,
    val fareBreakdown: String?,
    val timeAgo: String,
    val avatarUrl: String?
)

/**
 * Domain model for a past completed or cancelled ride in History.
 */
data class HistoryRideModel(
    val id: String,
    val origin: String,
    val destination: String,
    val dateTime: String,
    val price: Int,
    val status: String, // "Completed", "Cancelled"
    val isReviewed: Boolean = false,
    val userSubmittedRating: Int = 0,
    val userRole: String = "PASSENGER", // "PASSENGER" or "DRIVER"
    val reviewComment: String = ""
)

/**
 * Presentation model for a search result ride card.
 */
data class SearchRideItem(
    val id: String,
    val departureTime: String,
    val departureLocation: String,
    val viaStops: String,
    val duration: String,
    val arrivalTime: String,
    val arrivalLocation: String,
    val price: Int,
    val driverName: String,
    val driverRating: Double,
    val driverAvatar: String?,
    val seatsLeft: Int,
    val vehicleName: String = "Swift Dzire",
    val vehicleModel: String = "Swift Dzire",
    val vehiclePlate: String = "DL 01 AB 1234",
    val routeLocations: List<RouteLocation> = emptyList(),
    val initialSelectedStopIndex: Int = 0,
    val isDriverVerified: Boolean = true,
    val departureDistanceKm: Double = 0.5,
    val arrivalDistanceKm: Double = 0.5,
    val durationMinutes: Int = 120
)

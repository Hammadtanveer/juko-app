package com.juko.app.feature.postride.presentation

data class PostRideState(
    // Step 1: Route & Pricing
    val origin: String = "Mumbai",
    val destination: String = "Pune",
    val stops: List<String> = emptyList(),
    val departureDate: String = "Thu, 27 Aug",
    val departureTime: String = "10:00 AM",
    val arrivalDate: String = "Thu, 27 Aug",
    val arrivalTime: String = "01:30 PM",
    val journeyTime: String = "3h 30m",
    val pricePerSeat: Int = 450,
    val segmentPrices: Map<String, Int> = emptyMap(),
    val availableSeats: Int = 3,
    
    // Step 2: Preferences
    val vehicleName: String = "Hyundai Creta - MH 12 AB 1234",
    val luggageAllowed: Boolean = true,
    val acAvailable: Boolean = true,
    val maxTwoBackSeat: Boolean = true,
    val smokingAllowed: Boolean = false,
    val petsAllowed: Boolean = false,
    val roofCarrierAvailable: Boolean = false,
    val autoAccept: Boolean = false,
    val isSeatPreferencesEnabled: Boolean = false,
    val frontSeatPrice: Int = 50,
    val windowSeatPrice: Int = 30,
    val isWholeCarBookingEnabled: Boolean = false,
    val wholeCarPrice: Int = 1200,
    val notes: String = "",
    
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface PostRideEvent {
    // Step 1 Events
    data class OriginChanged(val value: String) : PostRideEvent
    data class DestinationChanged(val value: String) : PostRideEvent
    data class AddStop(val city: String) : PostRideEvent
    data class UpdateStop(val index: Int, val city: String) : PostRideEvent
    data class RemoveStop(val index: Int) : PostRideEvent
    data class SegmentPriceChanged(val segment: String, val price: Int) : PostRideEvent
    
    data class PriceChanged(val value: Int) : PostRideEvent
    data class SeatsChanged(val value: Int) : PostRideEvent
    data class DepartureDateChanged(val value: String) : PostRideEvent
    data class DepartureTimeChanged(val value: String) : PostRideEvent
    data class ArrivalDateChanged(val value: String) : PostRideEvent
    data class ArrivalTimeChanged(val value: String) : PostRideEvent
    
    // Step 2 Events
    data object ToggleLuggage : PostRideEvent
    data object ToggleAC : PostRideEvent
    data object ToggleMaxTwoBack : PostRideEvent
    data object ToggleSmoking : PostRideEvent
    data object TogglePets : PostRideEvent
    data object ToggleRoofCarrier : PostRideEvent
    data object ToggleAutoAccept : PostRideEvent
    data object ToggleSeatPreferences : PostRideEvent
    data class FrontSeatPriceChanged(val value: Int) : PostRideEvent
    data class WindowSeatPriceChanged(val value: Int) : PostRideEvent
    data object ToggleWholeCarBooking : PostRideEvent
    data class WholeCarPriceChanged(val value: Int) : PostRideEvent
    data class NotesChanged(val value: String) : PostRideEvent
    
    data object Submit : PostRideEvent
    data object SaveDraft : PostRideEvent
}

sealed interface PostRideSideEffect {
    data object NavigateToHome : PostRideSideEffect
    data class ShowToast(val message: String) : PostRideSideEffect
    data class ShowError(val message: String) : PostRideSideEffect
}

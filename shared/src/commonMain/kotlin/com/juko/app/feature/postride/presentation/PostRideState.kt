package com.juko.app.feature.postride.presentation

import com.juko.app.core.location.PlaceLocation
import com.juko.app.core.location.PlaceSuggestion
import com.juko.app.feature.profile.presentation.VehicleItem

data class PostRideState(
    // Step 1: Route & Pricing (Google Places & Destination-anchored)
    val origin: String = "Seohara",
    val originLocation: PlaceLocation? = PlaceLocation(
        name = "Seohara",
        placeId = "place_seo_01",
        secondaryText = "Bijnor, Uttar Pradesh, India",
        latitude = 29.2135,
        longitude = 78.5835,
        isConfirmed = true
    ),
    val originSuggestions: List<PlaceSuggestion> = emptyList(),

    val destination: String = "Delhi",
    val destinationLocation: PlaceLocation? = PlaceLocation(
        name = "Delhi",
        placeId = "place_del_01",
        secondaryText = "National Capital Territory of Delhi, India",
        latitude = 28.6139,
        longitude = 77.2090,
        isConfirmed = true
    ),
    val destinationSuggestions: List<PlaceSuggestion> = emptyList(),

    // Intermediate Pickup Points (formerly "Stops")
    val pickupPoints: List<PlaceLocation> = listOf(
        PlaceLocation(name = "Noorpur", placeId = "place_noo_01", secondaryText = "Bijnor, Uttar Pradesh, India", latitude = 29.1500, longitude = 78.4100, isConfirmed = true),
        PlaceLocation(name = "Chandpur", placeId = "place_cha_01", secondaryText = "Bijnor, Uttar Pradesh, India", latitude = 29.1367, longitude = 78.2736, isConfirmed = true)
    ),
    val activePickupSuggestions: Map<Int, List<PlaceSuggestion>> = emptyMap(),

    // Dynamic Date & Time
    val departureDate: String = "",
    val departureTime: String = "08:00 AM",
    val arrivalDate: String = "",
    val arrivalTime: String = "12:30 PM",
    val journeyTime: String = "4h 30m",

    // Pricing: Each boarding point -> Final Destination
    val pricePerSeat: Int = 450, // Origin -> Final Destination price
    val destinationPrices: Map<String, Int> = emptyMap(), // Key: Boarding City Name, Value: Price to Final Destination

    // Step 2: Preferences & Seats (Seats 1 to 6)
    val selectedVehicle: VehicleItem? = null,
    val vehicleName: String = "Toyota Camry - ABC-1234",
    val maxAvailableSeats: Int = 6, // Global fixed range: min 1, max 6
    val availableSeats: Int = 3,

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
    val validationError: String? = null,
    val error: String? = null
) {
    // Backwards-compatibility helper for legacy stops list
    val stops: List<String>
        get() = pickupPoints.map { it.name }
}

sealed interface PostRideEvent {
    // Step 1: Origin & Destination Autocomplete
    data class OriginQueryChanged(val query: String) : PostRideEvent
    data class OriginSelected(val suggestion: PlaceSuggestion) : PostRideEvent
    data class DestinationQueryChanged(val query: String) : PostRideEvent
    data class DestinationSelected(val suggestion: PlaceSuggestion) : PostRideEvent

    // Step 1: Pickup Points (formerly "Stops")
    data object AddPickupPoint : PostRideEvent
    data class PickupQueryChanged(val index: Int, val query: String) : PostRideEvent
    data class PickupSelected(val index: Int, val suggestion: PlaceSuggestion) : PostRideEvent
    data class RemovePickupPoint(val index: Int) : PostRideEvent

    // Step 1: Destination-Anchored Pricing
    data class DestinationPriceChanged(val boardingPoint: String, val price: Int) : PostRideEvent

    // Step 1: Schedule & Dates
    data class DepartureDateChanged(val value: String) : PostRideEvent
    data class DepartureTimeChanged(val value: String) : PostRideEvent
    data class ArrivalDateChanged(val value: String) : PostRideEvent
    data class ArrivalTimeChanged(val value: String) : PostRideEvent

    // Step 2: Vehicle & Seats
    data class VehicleSelected(val vehicle: VehicleItem) : PostRideEvent
    data class SeatsChanged(val value: Int) : PostRideEvent

    // Step 2: Preferences
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

    // Submission & Draft
    data object Submit : PostRideEvent
    data object SaveDraft : PostRideEvent
    data object ResetForm : PostRideEvent
}

sealed interface PostRideSideEffect {
    data object NavigateToHome : PostRideSideEffect
    data class NavigateToPublishedDetail(val rideId: String) : PostRideSideEffect
    data class ShowToast(val message: String) : PostRideSideEffect
    data class ShowError(val message: String) : PostRideSideEffect
}


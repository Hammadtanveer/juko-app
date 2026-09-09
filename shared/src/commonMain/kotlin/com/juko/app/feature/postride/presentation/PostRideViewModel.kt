package com.juko.app.feature.postride.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.juko.app.core.data.RideStateManager
import com.juko.app.core.location.PlaceLocation
import com.juko.app.core.location.PlaceSuggestion
import com.juko.app.core.location.PlacesAutocompleteService
import com.juko.app.feature.postride.domain.model.RideOffer
import com.juko.app.feature.postride.domain.usecase.PublishRideUseCase
import com.juko.app.feature.profile.domain.DriverProfileManager
import com.juko.app.feature.profile.presentation.VehicleItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class PostRideViewModel(
    private val publishRideUseCase: PublishRideUseCase,
    private val placesAutocompleteService: PlacesAutocompleteService
) : ScreenModel {

    private val _state = MutableStateFlow(PostRideState())
    val state = _state.asStateFlow()

    private val _effect = Channel<PostRideSideEffect>()
    val effect = _effect.receiveAsFlow()

    private var originSearchJob: Job? = null
    private var destinationSearchJob: Job? = null
    private val pickupSearchJobs = mutableMapOf<Int, Job>()
    private var routeCalcJob: Job? = null

    init {
        initializeDefaults()
    }

    private fun initializeDefaults() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val formattedDate = "${now.date.dayOfMonth} ${now.date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }}"
        
        val primaryVehicle = DriverProfileManager.vehicles.firstOrNull()
        // Driver occupies 1 seat; passenger seats offered = (total vehicle seating capacity - 1), clamped between 1 and 6
        val defaultSeats = primaryVehicle?.let { (it.seatingCapacity - 1).coerceIn(1, 6) } ?: 3

        _state.update {
            it.copy(
                departureDate = formattedDate,
                arrivalDate = formattedDate,
                selectedVehicle = primaryVehicle,
                vehicleName = primaryVehicle?.let { v -> "${v.brand} ${v.model} - ${v.plateNumber}" } ?: it.vehicleName,
                maxAvailableSeats = 6,
                availableSeats = defaultSeats
            )
        }
        updateDestinationPrices()
        recalculateRouteSchedule()
    }

    fun onEvent(event: PostRideEvent) {
        when (event) {
            // Origin Autocomplete
            is PostRideEvent.OriginQueryChanged -> {
                _state.update {
                    it.copy(
                        origin = event.query,
                        originLocation = if (it.originLocation?.name == event.query) it.originLocation else null
                    )
                }
                originSearchJob?.cancel()
                originSearchJob = screenModelScope.launch {
                    val suggestions = placesAutocompleteService.searchPlaces(event.query)
                    _state.update { it.copy(originSuggestions = suggestions) }
                }
            }
            is PostRideEvent.OriginSelected -> {
                val initialLoc = PlaceLocation(
                    name = event.suggestion.primaryText,
                    placeId = event.suggestion.placeId,
                    secondaryText = event.suggestion.secondaryText,
                    latitude = event.suggestion.latitude,
                    longitude = event.suggestion.longitude,
                    isConfirmed = true
                )
                _state.update {
                    it.copy(
                        origin = event.suggestion.primaryText,
                        originLocation = initialLoc,
                        originSuggestions = emptyList()
                    )
                }
                screenModelScope.launch {
                    val detailed = placesAutocompleteService.getPlaceDetails(event.suggestion.placeId)
                    if (detailed != null) {
                        _state.update { it.copy(originLocation = detailed) }
                        recalculateRouteSchedule()
                    }
                }
                updateDestinationPrices()
                recalculateRouteSchedule()
            }

            // Destination Autocomplete
            is PostRideEvent.DestinationQueryChanged -> {
                _state.update {
                    it.copy(
                        destination = event.query,
                        destinationLocation = if (it.destinationLocation?.name == event.query) it.destinationLocation else null
                    )
                }
                destinationSearchJob?.cancel()
                destinationSearchJob = screenModelScope.launch {
                    val suggestions = placesAutocompleteService.searchPlaces(event.query)
                    _state.update { it.copy(destinationSuggestions = suggestions) }
                }
            }
            is PostRideEvent.DestinationSelected -> {
                val initialLoc = PlaceLocation(
                    name = event.suggestion.primaryText,
                    placeId = event.suggestion.placeId,
                    secondaryText = event.suggestion.secondaryText,
                    latitude = event.suggestion.latitude,
                    longitude = event.suggestion.longitude,
                    isConfirmed = true
                )
                _state.update {
                    it.copy(
                        destination = event.suggestion.primaryText,
                        destinationLocation = initialLoc,
                        destinationSuggestions = emptyList()
                    )
                }
                screenModelScope.launch {
                    val detailed = placesAutocompleteService.getPlaceDetails(event.suggestion.placeId)
                    if (detailed != null) {
                        _state.update { it.copy(destinationLocation = detailed) }
                        recalculateRouteSchedule()
                    }
                }
                updateDestinationPrices()
                recalculateRouteSchedule()
            }

            // Pickup Points (formerly "Stops")
            is PostRideEvent.AddPickupPoint -> {
                _state.update {
                    it.copy(pickupPoints = it.pickupPoints + PlaceLocation(name = "", isConfirmed = false))
                }
                updateDestinationPrices()
            }
            is PostRideEvent.PickupQueryChanged -> {
                _state.update { current ->
                    val updated = current.pickupPoints.toMutableList()
                    if (event.index in updated.indices) {
                        val currentLoc = updated[event.index]
                        updated[event.index] = currentLoc.copy(
                            name = event.query,
                            isConfirmed = if (currentLoc.name == event.query) currentLoc.isConfirmed else false
                        )
                    }
                    current.copy(pickupPoints = updated)
                }
                pickupSearchJobs[event.index]?.cancel()
                pickupSearchJobs[event.index] = screenModelScope.launch {
                    val suggestions = placesAutocompleteService.searchPlaces(event.query)
                    _state.update { state ->
                        val map = state.activePickupSuggestions.toMutableMap()
                        map[event.index] = suggestions
                        state.copy(activePickupSuggestions = map)
                    }
                }
            }
            is PostRideEvent.PickupSelected -> {
                val initialLoc = PlaceLocation(
                    name = event.suggestion.primaryText,
                    placeId = event.suggestion.placeId,
                    secondaryText = event.suggestion.secondaryText,
                    latitude = event.suggestion.latitude,
                    longitude = event.suggestion.longitude,
                    isConfirmed = true
                )
                _state.update { current ->
                    val updated = current.pickupPoints.toMutableList()
                    if (event.index in updated.indices) {
                        updated[event.index] = initialLoc
                    }
                    val map = current.activePickupSuggestions.toMutableMap()
                    map.remove(event.index)
                    current.copy(pickupPoints = updated, activePickupSuggestions = map)
                }
                screenModelScope.launch {
                    val detailed = placesAutocompleteService.getPlaceDetails(event.suggestion.placeId)
                    if (detailed != null) {
                        _state.update { current ->
                            val updated = current.pickupPoints.toMutableList()
                            if (event.index in updated.indices) {
                                updated[event.index] = detailed
                            }
                            current.copy(pickupPoints = updated)
                        }
                        recalculateRouteSchedule()
                    }
                }
                updateDestinationPrices()
                recalculateRouteSchedule()
            }
            is PostRideEvent.RemovePickupPoint -> {
                _state.update { current ->
                    val updated = current.pickupPoints.toMutableList()
                    if (event.index in updated.indices) {
                        updated.removeAt(event.index)
                    }
                    val map = current.activePickupSuggestions.toMutableMap()
                    map.remove(event.index)
                    current.copy(pickupPoints = updated, activePickupSuggestions = map)
                }
                updateDestinationPrices()
                recalculateRouteSchedule()
            }

            // Destination-Anchored Pricing
            is PostRideEvent.DestinationPriceChanged -> {
                _state.update { current ->
                    val updated = current.destinationPrices.toMutableMap()
                    updated[event.boardingPoint] = event.price
                    val updatedBasePrice = if (event.boardingPoint == current.origin) event.price else current.pricePerSeat
                    current.copy(destinationPrices = updated, pricePerSeat = updatedBasePrice)
                }
            }

            // Schedule
            is PostRideEvent.DepartureDateChanged -> {
                _state.update { it.copy(departureDate = event.value) }
                recalculateRouteSchedule()
            }
            is PostRideEvent.DepartureTimeChanged -> {
                _state.update { it.copy(departureTime = event.value) }
                recalculateRouteSchedule()
            }
            is PostRideEvent.ArrivalDateChanged -> _state.update { it.copy(arrivalDate = event.value) }
            is PostRideEvent.ArrivalTimeChanged -> _state.update { it.copy(arrivalTime = event.value) }

            // Step 2: Vehicle & Seats
            is PostRideEvent.VehicleSelected -> {
                val passengerSeats = (event.vehicle.seatingCapacity - 1).coerceIn(1, 6)
                _state.update {
                    it.copy(
                        selectedVehicle = event.vehicle,
                        vehicleName = "${event.vehicle.brand} ${event.vehicle.model} - ${event.vehicle.plateNumber}",
                        availableSeats = passengerSeats
                    )
                }
            }
            is PostRideEvent.SeatsChanged -> {
                _state.update { it.copy(availableSeats = event.value.coerceIn(1, 6)) }
            }

            // Preferences
            is PostRideEvent.ToggleLuggage -> _state.update { it.copy(luggageAllowed = !it.luggageAllowed) }
            is PostRideEvent.ToggleAC -> _state.update { it.copy(acAvailable = !it.acAvailable) }
            is PostRideEvent.ToggleMaxTwoBack -> _state.update { it.copy(maxTwoBackSeat = !it.maxTwoBackSeat) }
            is PostRideEvent.ToggleSmoking -> _state.update { it.copy(smokingAllowed = !it.smokingAllowed) }
            is PostRideEvent.TogglePets -> _state.update { it.copy(petsAllowed = !it.petsAllowed) }
            is PostRideEvent.ToggleRoofCarrier -> _state.update { it.copy(roofCarrierAvailable = !it.roofCarrierAvailable) }
            is PostRideEvent.ToggleAutoAccept -> _state.update { it.copy(autoAccept = !it.autoAccept) }
            is PostRideEvent.ToggleSeatPreferences -> _state.update { it.copy(isSeatPreferencesEnabled = !it.isSeatPreferencesEnabled) }
            is PostRideEvent.FrontSeatPriceChanged -> _state.update { it.copy(frontSeatPrice = event.value) }
            is PostRideEvent.WindowSeatPriceChanged -> _state.update { it.copy(windowSeatPrice = event.value) }
            is PostRideEvent.ToggleWholeCarBooking -> _state.update { it.copy(isWholeCarBookingEnabled = !it.isWholeCarBookingEnabled) }
            is PostRideEvent.WholeCarPriceChanged -> _state.update { it.copy(wholeCarPrice = event.value) }
            is PostRideEvent.NotesChanged -> _state.update { it.copy(notes = event.value) }

            // Publish / Draft
            is PostRideEvent.Submit -> performPublish()
            is PostRideEvent.ResetForm -> resetForm()
            is PostRideEvent.SaveDraft -> {
                val published = RideStateManager.publishRide(
                    origin = _state.value.origin,
                    destination = _state.value.destination,
                    stops = _state.value.pickupPoints.map { it.name }.filter { it.isNotBlank() },
                    departureDate = _state.value.departureDate,
                    departureTime = _state.value.departureTime,
                    totalSeats = _state.value.availableSeats,
                    pricePerSeat = _state.value.pricePerSeat,
                    destinationPrices = _state.value.destinationPrices,
                    vehicleName = _state.value.vehicleName,
                    isDraft = true,
                    originLocation = _state.value.originLocation,
                    destinationLocation = _state.value.destinationLocation,
                    pickupLocations = _state.value.pickupPoints
                )
                screenModelScope.launch {
                    _effect.send(PostRideSideEffect.ShowToast("Draft saved successfully!"))
                    _effect.send(PostRideSideEffect.NavigateToPublishedDetail(published.id))
                }
            }
        }
    }

    fun resetForm() {
        _state.value = PostRideState()
        initializeDefaults()
    }

    /**
     * Updates destinationPrices to ensure every boarding point (Origin + each Pickup Point)
     * maps directly to the Final Destination price.
     * E.g.:
     * Seohara -> Delhi: ₹450
     * Noorpur -> Delhi: ₹350
     * Chandpur -> Delhi: ₹250
     */
    private fun updateDestinationPrices() {
        _state.update { current ->
            val boardingPoints = listOf(current.origin) + current.pickupPoints.map { it.name }.filter { it.isNotBlank() }
            val updatedMap = mutableMapOf<String, Int>()
            val totalPoints = boardingPoints.size

            boardingPoints.forEachIndexed { index, point ->
                val existing = current.destinationPrices[point]
                if (existing != null) {
                    updatedMap[point] = existing
                } else {
                    // Default staggered calculation decreasing towards final destination
                    val calculated = if (index == 0) {
                        current.pricePerSeat
                    } else {
                        val fraction = (totalPoints - index).toFloat() / totalPoints.toFloat()
                        ((current.pricePerSeat * fraction) / 10).toInt() * 10
                    }.coerceAtLeast(50)
                    updatedMap[point] = calculated
                }
            }

            current.copy(
                destinationPrices = updatedMap,
                pricePerSeat = updatedMap[current.origin] ?: current.pricePerSeat
            )
        }
    }

    fun validateStep1(): String? {
        val s = _state.value
        if (s.origin.isBlank()) return "Please enter and select a departure location"
        if (s.destination.isBlank()) return "Please enter and select a final destination"
        if (s.origin.trim().equals(s.destination.trim(), ignoreCase = true)) {
            return "Start location and destination cannot be the same"
        }

        val allPoints = listOf(s.origin.trim().lowercase()) +
                s.pickupPoints.map { it.name.trim().lowercase() }.filter { it.isNotBlank() }

        val duplicates = allPoints.groupingBy { it }.eachCount().filter { it.value > 1 }
        if (duplicates.isNotEmpty()) {
            return "Duplicate location '${duplicates.keys.first()}' found in route"
        }

        if (allPoints.contains(s.destination.trim().lowercase())) {
            return "Final destination cannot be added as a pickup point"
        }

        return null
    }

    private fun performPublish() {
        val validation = validateStep1()
        if (validation != null) {
            screenModelScope.launch {
                _effect.send(PostRideSideEffect.ShowError(validation))
            }
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val publishedRide = RideStateManager.publishRide(
                origin = _state.value.origin,
                destination = _state.value.destination,
                stops = _state.value.pickupPoints.map { it.name }.filter { it.isNotBlank() },
                departureDate = _state.value.departureDate,
                departureTime = _state.value.departureTime,
                totalSeats = _state.value.availableSeats,
                pricePerSeat = _state.value.pricePerSeat,
                destinationPrices = _state.value.destinationPrices,
                vehicleName = _state.value.vehicleName,
                isDraft = false,
                originLocation = _state.value.originLocation,
                destinationLocation = _state.value.destinationLocation,
                pickupLocations = _state.value.pickupPoints
            )

            val offer = RideOffer(
                driverId = "dr_001",
                origin = _state.value.origin,
                destination = _state.value.destination,
                departureTime = 0L,
                pricePerSeat = _state.value.pricePerSeat,
                availableSeats = _state.value.availableSeats,
                vehicleId = _state.value.selectedVehicle?.id ?: "vh_001",
                luggageAllowed = _state.value.luggageAllowed,
                acAvailable = _state.value.acAvailable,
                maxTwoBackSeat = _state.value.maxTwoBackSeat,
                smokingAllowed = _state.value.smokingAllowed,
                petsAllowed = _state.value.petsAllowed,
                notes = _state.value.notes,
                originLatitude = _state.value.originLocation?.latitude,
                originLongitude = _state.value.originLocation?.longitude,
                destinationLatitude = _state.value.destinationLocation?.latitude,
                destinationLongitude = _state.value.destinationLocation?.longitude,
                stops = _state.value.pickupPoints.map { it.name }.filter { it.isNotBlank() }
            )

            publishRideUseCase(offer)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(PostRideSideEffect.ShowToast("Ride published successfully!"))
                    _effect.send(PostRideSideEffect.NavigateToPublishedDetail(publishedRide.id))
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    _effect.send(PostRideSideEffect.ShowError(error.message ?: "Publishing failed"))
                }
        }
    }

    private fun recalculateRouteSchedule() {
        routeCalcJob?.cancel()
        routeCalcJob = screenModelScope.launch {
            val currentState = _state.value
            val durationMinutes = placesAutocompleteService.calculateRouteDurationMinutes(
                origin = currentState.origin,
                destination = currentState.destination,
                originLoc = currentState.originLocation,
                destinationLoc = currentState.destinationLocation,
                pickupPoints = currentState.pickupPoints
            )
            val (calculatedArrivalTime, calculatedArrivalDate, journeyTime) = calculateArrival(
                departureDate = currentState.departureDate,
                departureTime = currentState.departureTime,
                durationMinutes = durationMinutes
            )
            _state.update {
                it.copy(
                    arrivalTime = calculatedArrivalTime,
                    arrivalDate = calculatedArrivalDate,
                    journeyTime = journeyTime
                )
            }
        }
    }

    private fun calculateArrival(departureDate: String, departureTime: String, durationMinutes: Int): Triple<String, String, String> {
        val durationHours = durationMinutes / 60
        val durationMins = durationMinutes % 60
        val journeyTime = if (durationHours > 0) "${durationHours}h ${durationMins}m" else "${durationMins}m"

        val cleanTime = departureTime.trim().uppercase()
        val isPm = cleanTime.contains("PM")
        val isAm = cleanTime.contains("AM")
        val raw = cleanTime.replace("AM", "").replace("PM", "").trim()
        val parts = raw.split(":")
        val rawHour = parts.getOrNull(0)?.toIntOrNull() ?: 8
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val startHour = when {
            isPm && rawHour < 12 -> rawHour + 12
            isAm && rawHour == 12 -> 0
            else -> rawHour
        }

        val totalStartMins = startHour * 60 + minute
        val totalArrivalMins = totalStartMins + durationMinutes

        val daysOffset = totalArrivalMins / (24 * 60)
        val arrivalHour24 = (totalArrivalMins % (24 * 60)) / 60
        val arrivalMinute = totalArrivalMins % 60

        val arrAmPm = if (arrivalHour24 >= 12) "PM" else "AM"
        val arrHour12 = when {
            arrivalHour24 == 0 -> 12
            arrivalHour24 > 12 -> arrivalHour24 - 12
            else -> arrivalHour24
        }
        val arrivalTime = "${arrHour12.toString().padStart(2, '0')}:${arrivalMinute.toString().padStart(2, '0')} $arrAmPm"

        val arrivalDate = if (daysOffset == 0) {
            departureDate.ifBlank { "Today" }
        } else {
            offsetDateString(departureDate, daysOffset)
        }

        return Triple(arrivalTime, arrivalDate, journeyTime)
    }

    private fun offsetDateString(dateStr: String, daysOffset: Int): String {
        val trimmed = dateStr.trim()
        if (trimmed.equals("Today", ignoreCase = true)) {
            return if (daysOffset == 1) "Tomorrow" else "+$daysOffset days"
        }
        val parts = trimmed.split(" ")
        val dayNum = parts.firstOrNull()?.toIntOrNull()
        val monthStr = parts.getOrNull(1)
        if (dayNum != null && monthStr != null) {
            return "${dayNum + daysOffset} $monthStr"
        }
        return "$trimmed (+$daysOffset d)"
    }
}

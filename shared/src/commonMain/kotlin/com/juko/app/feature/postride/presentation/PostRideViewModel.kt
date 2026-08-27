package com.juko.app.feature.postride.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.juko.app.feature.postride.domain.model.RideOffer
import com.juko.app.feature.postride.domain.usecase.PublishRideUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class PostRideViewModel(
    private val publishRideUseCase: PublishRideUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(PostRideState())
    val state = _state.asStateFlow()

    private val _effect = Channel<PostRideSideEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        updateSegments()
    }

    fun onEvent(event: PostRideEvent) {
        when (event) {
            is PostRideEvent.OriginChanged -> {
                _state.update { it.copy(origin = event.value) }
                updateSegments()
            }
            is PostRideEvent.DestinationChanged -> {
                _state.update { it.copy(destination = event.value) }
                updateSegments()
            }
            is PostRideEvent.AddStop -> {
                _state.update { it.copy(stops = it.stops + event.city) }
                updateSegments()
            }
            is PostRideEvent.UpdateStop -> {
                _state.update { 
                    val newStops = it.stops.toMutableList()
                    newStops[event.index] = event.city
                    it.copy(stops = newStops)
                }
                updateSegments()
            }
            is PostRideEvent.RemoveStop -> {
                _state.update { it.copy(stops = it.stops.toMutableList().apply { removeAt(event.index) }) }
                updateSegments()
            }
            is PostRideEvent.SegmentPriceChanged -> {
                _state.update { 
                    val newPrices = it.segmentPrices.toMutableMap()
                    newPrices[event.segment] = event.price
                    it.copy(segmentPrices = newPrices)
                }
            }
            is PostRideEvent.PriceChanged -> _state.update { it.copy(pricePerSeat = event.value) }
            is PostRideEvent.SeatsChanged -> _state.update { it.copy(availableSeats = event.value) }
            is PostRideEvent.DepartureDateChanged -> _state.update { it.copy(departureDate = event.value) }
            is PostRideEvent.DepartureTimeChanged -> _state.update { it.copy(departureTime = event.value) }
            is PostRideEvent.ArrivalDateChanged -> _state.update { it.copy(arrivalDate = event.value) }
            is PostRideEvent.ArrivalTimeChanged -> _state.update { it.copy(arrivalTime = event.value) }
            
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
            
            is PostRideEvent.Submit -> performPublish()
            is PostRideEvent.SaveDraft -> {
                screenModelScope.launch {
                    _effect.send(PostRideSideEffect.ShowToast("Draft saved!"))
                    _effect.send(PostRideSideEffect.NavigateToHome)
                }
            }
        }
    }

    private fun updateSegments() {
        _state.update { state ->
            val allPoints = listOf(state.origin) + state.stops + listOf(state.destination)
            val segments = mutableMapOf<String, Int>()
            for (i in 0 until allPoints.size - 1) {
                val segment = "${allPoints[i]} → ${allPoints[i+1]}"
                segments[segment] = state.segmentPrices[segment] ?: 250 // Default price
            }
            state.copy(segmentPrices = segments)
        }
    }

    private fun performPublish() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val offer = RideOffer(
                driverId = "dr_001",
                origin = _state.value.origin,
                destination = _state.value.destination,
                departureTime = 0L, // Mock time
                pricePerSeat = _state.value.pricePerSeat,
                availableSeats = _state.value.availableSeats,
                vehicleId = "vh_001",
                luggageAllowed = _state.value.luggageAllowed,
                acAvailable = _state.value.acAvailable,
                maxTwoBackSeat = _state.value.maxTwoBackSeat,
                smokingAllowed = _state.value.smokingAllowed,
                petsAllowed = _state.value.petsAllowed,
                notes = _state.value.notes
            )

            publishRideUseCase(offer)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(PostRideSideEffect.ShowToast("Ride published successfully!"))
                    _effect.send(PostRideSideEffect.NavigateToHome)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    _effect.send(PostRideSideEffect.ShowError(error.message ?: "Publishing failed"))
                }
        }
    }
}

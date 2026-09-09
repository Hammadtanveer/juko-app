package com.juko.app.core.data

import com.juko.app.core.location.PlaceLocation
import com.juko.app.core.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Central state manager for rides, bookings, requests, and history.
 * Provides unified reactive state and business logic across all screens.
 */
object RideStateManager {

    private val _publishedRides = MutableStateFlow(getInitialPublishedRides())
    val publishedRides: StateFlow<List<PublishedRideModel>> = _publishedRides.asStateFlow()

    private val _customerBookings = MutableStateFlow(getInitialBookings())
    val customerBookings: StateFlow<List<CustomerBookingModel>> = _customerBookings.asStateFlow()

    private val _bookingRequests = MutableStateFlow(getInitialRequests())
    val bookingRequests: StateFlow<List<BookingRequestModel>> = _bookingRequests.asStateFlow()

    private val _rideHistory = MutableStateFlow(getInitialHistory())
    val rideHistory: StateFlow<List<HistoryRideModel>> = _rideHistory.asStateFlow()

    // Active sub-tab in MyRidesScreen (0: PUBLISH, 1: REQUEST/BOOKING, 2: HISTORY)
    private val _selectedRidesTab = MutableStateFlow(0)
    val selectedRidesTab: StateFlow<Int> = _selectedRidesTab.asStateFlow()

    fun selectRidesTab(tabIndex: Int) {
        _selectedRidesTab.value = tabIndex
    }

    /**
     * Search active published rides matching origin/stop and destination.
     * Supports boarding at intermediate stops along the route.
     */
    fun searchRides(origin: String, destination: String): List<SearchRideItem> {
        val queryOrigin = origin.trim().lowercase()
        val queryDestination = destination.trim().lowercase()

        val allActive = _publishedRides.value.filter { it.status.equals("Active", ignoreCase = true) }

        if (queryOrigin.isBlank() && queryDestination.isBlank()) {
            return allActive.map { it.toSearchRideItem(0) }
        }

        val results = mutableListOf<SearchRideItem>()

        for (ride in allActive) {
            val route = ride.routeLocations
            if (route.isEmpty()) continue

            // Find matching destination index (must be at or after boarding stop)
            val destIndex = if (queryDestination.isNotBlank()) {
                route.indexOfLast { it.name.lowercase().contains(queryDestination) }
            } else {
                route.lastIndex
            }

            if (destIndex == -1) continue // Destination not found on this route

            // Find matching origin/boarding stop index (must be strictly before destination)
            val originIndex = if (queryOrigin.isNotBlank()) {
                route.indexOfFirst { it.name.lowercase().contains(queryOrigin) }
            } else {
                0
            }

            if (originIndex != -1 && originIndex < destIndex) {
                // Valid forward segment!
                results.add(ride.toSearchRideItem(originIndex))
            }
        }

        return results
    }

    /**
     * Book seats on a ride.
     * Decrements available seats, creates confirmed booking, adds passenger to driver's list,
     * and locks ride editing if filledSeats > 0.
     */
    fun bookRide(
        rideId: String,
        pickupStopIndex: Int,
        seats: Int,
        isFrontSeat: Boolean = false,
        isWindowSeat: Boolean = false,
        passengerName: String = "Alex Rivera"
    ): Result<CustomerBookingModel> {
        val ride = _publishedRides.value.find { it.id == rideId }
        val routeLocations = ride?.routeLocations ?: emptyList()
        val availableSeats = if (ride != null) ride.totalSeats - ride.filledSeats else 4

        if (seats > availableSeats) {
            return Result.failure(IllegalStateException("Not enough seats available ($availableSeats left)"))
        }

        val boardingLoc = routeLocations.getOrNull(pickupStopIndex)?.name
            ?: ride?.origin
            ?: "Pickup Stop"

        val destLoc = routeLocations.lastOrNull()?.name
            ?: ride?.destination
            ?: "Destination"

        val baseFullPrice = ride?.pricePerSeat ?: 450
        val pricePerSeat = RouteHelper.calculateRidePrice(routeLocations, pickupStopIndex, baseFullPrice)
        val extraCost = (if (isFrontSeat) 50 else 0) + (if (isWindowSeat) 30 else 0)
        val totalFare = (pricePerSeat * seats) + extraCost

        val newBookingId = "bk_${(1000..9999).random()}"
        val pin = (1000..9999).random().toString()

        val booking = CustomerBookingModel(
            id = newBookingId,
            rideId = rideId,
            origin = ride?.origin ?: boardingLoc,
            boardingStop = boardingLoc,
            destination = destLoc,
            departureDate = ride?.departureDate ?: "Today",
            departureTime = routeLocations.getOrNull(pickupStopIndex)?.estimatedTime.takeIf { !it.isNullOrBlank() }
                ?: ride?.departureTime ?: "08:00 AM",
            driverName = ride?.driverName ?: "Alex Rivera",
            driverAvatar = ride?.driverAvatar,
            vehicleName = ride?.vehicleName ?: "Swift Dzire",
            seatsBooked = seats,
            totalFare = totalFare,
            boardingPin = pin,
            status = "Confirmed"
        )

        // Update driver's published ride
        if (ride != null) {
            _publishedRides.update { list ->
                list.map { pub ->
                    if (pub.id == rideId) {
                        val newPassengers = pub.passengersList + PassengerEntry(
                            id = "p_${(1000..9999).random()}",
                            name = passengerName,
                            boardingStop = boardingLoc,
                            seats = seats,
                            totalFare = totalFare
                        )
                        pub.copy(
                            filledSeats = (pub.filledSeats + seats).coerceAtMost(pub.totalSeats),
                            passengersList = newPassengers
                        )
                    } else pub
                }
            }
        }

        // Add to customer bookings
        _customerBookings.update { listOf(booking) + it }

        return Result.success(booking)
    }

    /**
     * Publish a new ride from the driver's Post Ride flow.
     */
    fun publishRide(
        origin: String,
        destination: String,
        stops: List<String>,
        departureDate: String = "Today",
        departureTime: String = "08:00 AM",
        totalSeats: Int = 4,
        pricePerSeat: Int = 450,
        destinationPrices: Map<String, Int> = emptyMap(),
        vehicleName: String = "Swift Dzire",
        isDraft: Boolean = false,
        originLocation: PlaceLocation? = null,
        destinationLocation: PlaceLocation? = null,
        pickupLocations: List<PlaceLocation> = emptyList()
    ): PublishedRideModel {
        val newId = "ride_${(1000..9999).random()}"
        val cleanStops = stops.filter { it.isNotBlank() }

        // Build route locations with coordinates
        val routeLocations = mutableListOf<RouteLocation>()
        routeLocations.add(
            RouteLocation(
                id = "loc_${newId}_0",
                name = origin.ifBlank { "Origin" },
                order = 0,
                isSource = true,
                estimatedTime = departureTime,
                latitude = originLocation?.latitude,
                longitude = originLocation?.longitude
            )
        )
        cleanStops.forEachIndexed { index, stopName ->
            val pickupLoc = pickupLocations.getOrNull(index)
            routeLocations.add(
                RouteLocation(
                    id = "loc_${newId}_${index + 1}",
                    name = stopName,
                    order = index + 1,
                    estimatedTime = calculateEstTime(departureTime, index + 1),
                    latitude = pickupLoc?.latitude,
                    longitude = pickupLoc?.longitude
                )
            )
        }
        routeLocations.add(
            RouteLocation(
                id = "loc_${newId}_${cleanStops.size + 1}",
                name = destination.ifBlank { "Destination" },
                order = cleanStops.size + 1,
                isDestination = true,
                estimatedTime = calculateEstTime(departureTime, cleanStops.size + 1),
                latitude = destinationLocation?.latitude,
                longitude = destinationLocation?.longitude
            )
        )

        val newRide = PublishedRideModel(
            id = newId,
            origin = origin.ifBlank { "Origin" },
            destination = destination.ifBlank { "Destination" },
            intermediateStops = cleanStops,
            dateTime = "$departureDate, $departureTime",
            departureTime = departureTime,
            departureDate = departureDate,
            status = if (isDraft) "Draft" else "Active",
            filledSeats = 0, // Starts at 0, so editing is allowed!
            totalSeats = totalSeats,
            pricePerSeat = pricePerSeat,
            vehicleName = vehicleName,
            driverName = "Alex Rivera",
            driverAvatar = null,
            driverRating = 4.9,
            routeLocations = routeLocations,
            destinationPrices = destinationPrices,
            passengersList = emptyList()
        )

        _publishedRides.update { listOf(newRide) + it }
        return newRide
    }

    /**
     * Cancel an active passenger booking.
     */
    fun cancelBooking(bookingId: String): Boolean {
        val booking = _customerBookings.value.find { it.id == bookingId } ?: return false

        _customerBookings.update { list -> list.filter { it.id != bookingId } }

        // Restore seats on driver's published ride
        if (booking.rideId.isNotBlank()) {
            _publishedRides.update { list ->
                list.map { pub ->
                    if (pub.id == booking.rideId) {
                        pub.copy(
                            filledSeats = (pub.filledSeats - booking.seatsBooked).coerceAtLeast(0),
                            passengersList = pub.passengersList.filter { it.boardingStop != booking.boardingStop }
                        )
                    } else pub
                }
            }
        }

        // Add cancelled record to history
        val hist = HistoryRideModel(
            id = "hist_${(1000..9999).random()}",
            origin = booking.boardingStop,
            destination = booking.destination,
            dateTime = "${booking.departureDate}, ${booking.departureTime}",
            price = booking.totalFare,
            status = "Cancelled",
            userRole = "PASSENGER"
        )
        _rideHistory.update { listOf(hist) + it }

        return true
    }

    /**
     * Update an existing published ride.
     */
    fun updatePublishedRide(
        rideId: String,
        origin: String? = null,
        destination: String? = null,
        intermediateStops: List<String>? = null,
        departureDate: String? = null,
        departureTime: String? = null,
        totalSeats: Int? = null,
        pricePerSeat: Int? = null,
        destinationPrices: Map<String, Int>? = null
    ): Boolean {
        var updated = false
        _publishedRides.update { list ->
            list.map { ride ->
                if (ride.id == rideId) {
                    updated = true
                    val newOrigin = origin ?: ride.origin
                    val newDest = destination ?: ride.destination
                    val newDate = departureDate ?: ride.departureDate
                    val newTime = departureTime ?: ride.departureTime
                    val newPrice = pricePerSeat ?: ride.pricePerSeat
                    val newSeats = totalSeats ?: ride.totalSeats
                    val newStops = intermediateStops ?: ride.intermediateStops
                    val newDestPrices = destinationPrices ?: ride.destinationPrices

                    val routeLocations = mutableListOf<RouteLocation>()
                    routeLocations.add(
                        RouteLocation(
                            id = "loc_${rideId}_0",
                            name = newOrigin,
                            order = 0,
                            isSource = true,
                            estimatedTime = newTime
                        )
                    )
                    newStops.forEachIndexed { index, stopName ->
                        routeLocations.add(
                            RouteLocation(
                                id = "loc_${rideId}_${index + 1}",
                                name = stopName,
                                order = index + 1,
                                estimatedTime = calculateEstTime(newTime, index + 1)
                            )
                        )
                    }
                    routeLocations.add(
                        RouteLocation(
                            id = "loc_${rideId}_${newStops.size + 1}",
                            name = newDest,
                            order = newStops.size + 1,
                            isDestination = true,
                            estimatedTime = calculateEstTime(newTime, newStops.size + 1)
                        )
                    )

                    ride.copy(
                        origin = newOrigin,
                        destination = newDest,
                        intermediateStops = newStops,
                        departureDate = newDate,
                        departureTime = newTime,
                        dateTime = "$newDate, $newTime",
                        pricePerSeat = newPrice,
                        totalSeats = newSeats,
                        routeLocations = routeLocations,
                        destinationPrices = newDestPrices
                    )
                } else ride
            }
        }
        return updated
    }

    /**
     * Delete or cancel a published ride.
     */
    fun deletePublishedRide(rideId: String): Boolean {
        val ride = _publishedRides.value.find { it.id == rideId } ?: return false
        _publishedRides.update { list -> list.filter { it.id != rideId } }

        val hist = HistoryRideModel(
            id = "hist_${(1000..9999).random()}",
            origin = ride.origin,
            destination = ride.destination,
            dateTime = ride.dateTime,
            price = ride.pricePerSeat,
            status = "Cancelled",
            userRole = "DRIVER"
        )
        _rideHistory.update { listOf(hist) + it }

        return true
    }

    /**
     * Accept incoming passenger booking request.
     */
    fun acceptRequest(requestId: String): Boolean {
        val req = _bookingRequests.value.find { it.id == requestId } ?: return false
        _bookingRequests.update { list -> list.filter { it.id != requestId } }

        // Find matching published ride
        _publishedRides.update { list ->
            list.map { pub ->
                val matchesOrigin = req.routeStops.firstOrNull()?.let { pub.origin.contains(it, ignoreCase = true) } ?: false
                if (matchesOrigin || pub.id == req.rideId) {
                    val newPassenger = PassengerEntry(
                        id = "p_${(1000..9999).random()}",
                        name = req.passengerName,
                        boardingStop = req.pickupStation,
                        seats = req.seatsRequested,
                        totalFare = req.toEarn
                    )
                    pub.copy(
                        filledSeats = (pub.filledSeats + req.seatsRequested).coerceAtMost(pub.totalSeats),
                        passengersList = pub.passengersList + newPassenger
                    )
                } else pub
            }
        }
        return true
    }

    /**
     * Reject incoming passenger booking request.
     */
    fun rejectRequest(requestId: String): Boolean {
        _bookingRequests.update { list -> list.filter { it.id != requestId } }
        return true
    }

    /**
     * Submit review for a completed ride.
     */
    fun submitReview(rideId: String, rating: Int, comment: String): Boolean {
        _rideHistory.update { list ->
            list.map { hist ->
                if (hist.id == rideId) {
                    hist.copy(isReviewed = true, userSubmittedRating = rating, reviewComment = comment)
                } else hist
            }
        }
        return true
    }

    private fun calculateEstTime(baseTime: String, offsetHours: Int): String {
        val parts = baseTime.split(" ")
        val timeParts = parts.firstOrNull()?.split(":")
        val amPm = parts.getOrNull(1) ?: "AM"
        val hour = (timeParts?.firstOrNull()?.toIntOrNull() ?: 8) + offsetHours
        val minute = timeParts?.getOrNull(1) ?: "00"
        val normalizedHour = if (hour > 12) hour - 12 else hour
        val newAmPm = if (hour >= 12 && amPm == "AM") "PM" else amPm
        return "${normalizedHour.toString().padStart(2, '0')}:$minute $newAmPm"
    }

    private fun PublishedRideModel.toSearchRideItem(selectedStopIndex: Int): SearchRideItem {
        val route = this.routeLocations
        val startLoc = route.getOrNull(selectedStopIndex) ?: route.firstOrNull()
        val endLoc = route.lastOrNull()

        val viaList = route.drop(selectedStopIndex + 1).dropLast(1).map { it.name }
        val viaStops = if (viaList.isNotEmpty()) viaList.joinToString(", ") else "Direct Route"

        val effectivePrice = RouteHelper.calculateRidePrice(route, selectedStopIndex, this.pricePerSeat)
        val durationMins = (route.size - selectedStopIndex).coerceAtLeast(1) * 75
        val durationStr = "${durationMins / 60}h ${if (durationMins % 60 == 0) "00m" else "${durationMins % 60}m"}"
        val seatsRemaining = (this.totalSeats - this.filledSeats).coerceAtLeast(0)

        return SearchRideItem(
            id = this.id,
            departureTime = startLoc?.estimatedTime.takeIf { !it.isNullOrBlank() } ?: this.departureTime,
            departureLocation = startLoc?.name ?: this.origin,
            viaStops = viaStops,
            duration = durationStr,
            arrivalTime = endLoc?.estimatedTime.takeIf { !it.isNullOrBlank() } ?: "12:30 PM",
            arrivalLocation = endLoc?.name ?: this.destination,
            price = effectivePrice,
            driverName = this.driverName,
            driverRating = this.driverRating,
            driverAvatar = this.driverAvatar,
            seatsLeft = seatsRemaining,
            vehicleName = this.vehicleName,
            routeLocations = this.routeLocations,
            initialSelectedStopIndex = selectedStopIndex,
            isDriverVerified = this.isDriverVerified,
            departureDistanceKm = if (selectedStopIndex == 0) 0.4 else 1.2,
            arrivalDistanceKm = 0.5,
            durationMinutes = durationMins
        )
    }

    private fun getInitialPublishedRides(): List<PublishedRideModel> {
        val ride1Route = listOf(
            RouteLocation("loc_1_0", "Seohara", 0, isSource = true, estimatedTime = "08:00 AM", priceFromPrevious = 0),
            RouteLocation("loc_1_1", "Noorpur", 1, estimatedTime = "08:35 AM", priceFromPrevious = 80),
            RouteLocation("loc_1_2", "Chandpur", 2, estimatedTime = "09:15 AM", priceFromPrevious = 100),
            RouteLocation("loc_1_3", "Gajraula", 3, estimatedTime = "10:10 AM", priceFromPrevious = 120),
            RouteLocation("loc_1_4", "Delhi", 4, isDestination = true, estimatedTime = "12:30 PM", priceFromPrevious = 150)
        )

        val ride2Route = listOf(
            RouteLocation("loc_2_0", "Seohara", 0, isSource = true, estimatedTime = "08:15 AM", priceFromPrevious = 0),
            RouteLocation("loc_2_1", "Chandpur", 1, estimatedTime = "09:20 AM", priceFromPrevious = 140),
            RouteLocation("loc_2_2", "Delhi", 2, isDestination = true, estimatedTime = "12:30 PM", priceFromPrevious = 240)
        )

        val ride3Route = listOf(
            RouteLocation("loc_3_0", "Delhi", 0, isSource = true, estimatedTime = "07:00 AM", priceFromPrevious = 0),
            RouteLocation("loc_3_1", "Mathura", 1, estimatedTime = "09:15 AM", priceFromPrevious = 200),
            RouteLocation("loc_3_2", "Agra", 2, isDestination = true, estimatedTime = "11:00 AM", priceFromPrevious = 150)
        )

        return listOf(
            PublishedRideModel(
                id = "pub_1",
                origin = "Seohara",
                destination = "Delhi",
                intermediateStops = listOf("Noorpur", "Chandpur", "Gajraula"),
                dateTime = "Tomorrow, 08:00 AM",
                departureTime = "08:00 AM",
                departureDate = "Tomorrow",
                status = "Active",
                filledSeats = 2,
                totalSeats = 4,
                pricePerSeat = 450,
                vehicleName = "Swift Dzire",
                driverName = "Rahul Sharma",
                driverAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuCKYEb47azE7KIsX7pIzB9mjz1RKlj_e8gPNrELvoNxr4a4ZbO81La7WXxWwGuBD-2oQWPHrwDuTRXv1G8uEuA-RFEFlIrMbuUqxPqEyND6lnxpPkr390ck8Lk66bqK1ziDQZwg5V9JSommvmFtM0wURjqHnMp9lErkm5-rTMsXV6xmevXkm-vngAc2TmsP1ntYMnk-QMM6UNewnh-dVrA9XA3G7Y1Td4TGZpheU9qWZsJ0O5IwK7ZU",
                driverRating = 4.8,
                routeLocations = ride1Route,
                passengersList = listOf(
                    PassengerEntry(name = "Sarah Jenkins", boardingStop = "Chandpur", seats = 1, totalFare = 370),
                    PassengerEntry(name = "Amit Roy", boardingStop = "Noorpur", seats = 1, totalFare = 450)
                )
            ),
            PublishedRideModel(
                id = "pub_2",
                origin = "Seohara",
                destination = "Delhi",
                intermediateStops = listOf("Chandpur"),
                dateTime = "Today, 08:15 AM",
                departureTime = "08:15 AM",
                departureDate = "Today",
                status = "Active",
                filledSeats = 0, // 0 bookings -> EDIT ALLOWED!
                totalSeats = 3,
                pricePerSeat = 380,
                vehicleName = "Honda City",
                driverName = "Anita Kapoor",
                driverAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuDjCFi6hSeikXO26byFKauht4PmxZK204AR3XCqdXpOuM5L__mJ2cTmXDvEcl_G59mMY5F1ZCFx3mDLA5t_LhbfFRCcVN0OADih56H0naDNOo8O80lHswNiCLVi9_wrMvpla3t4r3yZ9nfpKnmLpJJPO7F1Xqg4V1JoPCrzXjc5--k7En9ONj0L9ibdyah-3MncNX0gjvGcHgaPoTqSHFKGhFOl92QEL9O7jfz4ixs01mRBBK9SaEN0",
                driverRating = 4.9,
                routeLocations = ride2Route,
                passengersList = emptyList()
            ),
            PublishedRideModel(
                id = "pub_3",
                origin = "Delhi",
                destination = "Agra",
                intermediateStops = listOf("Mathura"),
                dateTime = "22 Sep, 07:00 AM",
                departureTime = "07:00 AM",
                departureDate = "22 Sep",
                status = "Active",
                filledSeats = 0, // 0 bookings -> EDIT ALLOWED!
                totalSeats = 4,
                pricePerSeat = 350,
                vehicleName = "Hyundai Creta",
                driverName = "Alex Rivera",
                driverAvatar = null,
                driverRating = 4.8,
                routeLocations = ride3Route,
                passengersList = emptyList()
            )
        )
    }

    private fun getInitialBookings(): List<CustomerBookingModel> {
        return listOf(
            CustomerBookingModel(
                id = "bk_1",
                rideId = "pub_1",
                origin = "Seohara",
                boardingStop = "Chandpur",
                destination = "Delhi",
                departureDate = "Tomorrow",
                departureTime = "09:15 AM",
                driverName = "Rahul Sharma",
                driverAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuCKYEb47azE7KIsX7pIzB9mjz1RKlj_e8gPNrELvoNxr4a4ZbO81La7WXxWwGuBD-2oQWPHrwDuTRXv1G8uEuA-RFEFlIrMbuUqxPqEyND6lnxpPkr390ck8Lk66bqK1ziDQZwg5V9JSommvmFtM0wURjqHnMp9lErkm5-rTMsXV6xmevXkm-vngAc2TmsP1ntYMnk-QMM6UNewnh-dVrA9XA3G7Y1Td4TGZpheU9qWZsJ0O5IwK7ZU",
                vehicleName = "Swift Dzire",
                seatsBooked = 2,
                totalFare = 740,
                boardingPin = "7419",
                status = "Confirmed"
            )
        )
    }

    private fun getInitialRequests(): List<BookingRequestModel> {
        return listOf(
            BookingRequestModel(
                id = "req_1",
                rideId = "pub_1",
                passengerName = "Sarah Jenkins",
                rating = 4.8,
                ridesCount = 12,
                seatsRequested = 2,
                distanceAway = "0.8 km",
                pickupStation = "Chandpur Stop",
                routeStops = listOf("Seohara", "Noorpur", "Chandpur", "Delhi"),
                date = "Tomorrow",
                timeSlot = "09:15 AM",
                isWholeCar = false,
                seatPreference = "Front Seat",
                toEarn = 740,
                fareBreakdown = "₹700 Fare + ₹40 Add-on",
                timeAgo = "10 min ago",
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCuDOzLNc6hwnmc3ku7wiUEVxN-jxlnftdYPPrV3wORA0UJGq68VyDCef6aZZA502iroF4IilCFiWoTaB2A4k0n7qziv0ciVr86CcR57yJjxTmoqayhtopywImSelng0h-iBy-R-gI0xBFp5Hc5CAL5HLBo_0wM6lQbpy5D_c62kPYzxRu-QW6JtYOlykVUPZznRwESBNY3dcQlktNzNNo7spqZ2crHGxy3TRCPi-r4bdN9FJ9bcaxq"
            )
        )
    }

    private fun getInitialHistory(): List<HistoryRideModel> {
        return listOf(
            HistoryRideModel("h1", "Seohara", "Delhi", "15 Aug, 08:00 AM", 450, "Completed", isReviewed = false, userRole = "PASSENGER"),
            HistoryRideModel("h2", "Chandpur", "Delhi", "10 Aug, 09:15 AM", 370, "Completed", isReviewed = true, userSubmittedRating = 5, userRole = "PASSENGER", reviewComment = "Very smooth drive and on-time pickup!"),
            HistoryRideModel("h3", "Delhi", "Agra", "02 Aug, 07:00 AM", 500, "Cancelled", isReviewed = false, userRole = "DRIVER")
        )
    }
}

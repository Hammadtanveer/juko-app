package com.juko.app.core.model

/**
 * Represents a single point along a ride's chronological route.
 * Order strictly starts at 0 for Source, increments for each intermediate pickup stop,
 * and ends at N for the Final Destination.
 */
data class RouteLocation(
    val id: String,
    val name: String,
    val order: Int,
    val isSource: Boolean = false,
    val isDestination: Boolean = false,
    val estimatedTime: String = "",
    val priceFromPrevious: Int = 0,
    val latitude: Double? = null,
    val longitude: Double? = null
)

/**
 * Represents the presentation state of a RouteLocation when a passenger
 * chooses a boarding/pickup stop.
 */
data class RouteLocationState(
    val location: RouteLocation,
    val isSelected: Boolean,
    val isAvailable: Boolean,
    val isDisabled: Boolean
)

object RouteHelper {
    /**
     * Returns the state for each location along the route based on the selected pickup index.
     * Rule: Forward-only travel.
     * - Any location before index (< selectedPickupIndex) is marked as `isDisabled = true`.
     * - Location at index is `isSelected = true`.
     * - Locations after index (> selectedPickupIndex) are `isAvailable = true`.
     */
    fun getRouteLocationStates(
        routeLocations: List<RouteLocation>,
        selectedPickupIndex: Int
    ): List<RouteLocationState> {
        if (routeLocations.isEmpty()) return emptyList()
        val maxPickupIndex = (routeLocations.size - 2).coerceAtLeast(0)
        val safeIndex = selectedPickupIndex.coerceIn(0, maxPickupIndex)

        return routeLocations.mapIndexed { index, loc ->
            RouteLocationState(
                location = loc,
                isSelected = index == safeIndex,
                isAvailable = index >= safeIndex,
                isDisabled = index < safeIndex
            )
        }
    }

    /**
     * Calculates the price from selected pickup index to final destination.
     * Uses segment prices if provided, otherwise falls back to proportional calculation.
     */
    fun calculateRidePrice(
        routeLocations: List<RouteLocation>,
        selectedPickupIndex: Int,
        baseFullPrice: Int
    ): Int {
        if (routeLocations.isEmpty()) return baseFullPrice
        val totalStops = routeLocations.size - 1
        if (totalStops <= 0) return baseFullPrice
        val maxPickupIndex = (routeLocations.size - 2).coerceAtLeast(0)
        val safeIndex = selectedPickupIndex.coerceIn(0, maxPickupIndex)

        // Sum segment prices if defined
        val segmentSum = routeLocations.filterIndexed { index, _ -> index > safeIndex }
            .sumOf { it.priceFromPrevious }

        if (segmentSum > 0) {
            return segmentSum
        }

        // Proportional fallback based on remaining segment count
        val remainingSegments = totalStops - safeIndex
        val fraction = remainingSegments.toDouble() / totalStops.toDouble()
        return (baseFullPrice * fraction).toInt().coerceAtLeast(50)
    }
}

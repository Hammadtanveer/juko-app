package com.juko.app.core.location

data class RouteDirectionsResult(
    val durationSeconds: Long,
    val distanceMeters: Long,
    val durationText: String
)

/**
 * Platform-specific Google Places network client.
 */
expect class GooglePlacesDataSource() {
    suspend fun fetchPredictions(
        query: String,
        apiKey: String,
        sessionToken: String
    ): List<PlaceSuggestion>

    suspend fun fetchPlaceDetails(
        placeId: String,
        apiKey: String,
        sessionToken: String
    ): PlaceLocation?

    suspend fun calculateRouteDirections(
        origin: String,
        destination: String,
        waypoints: List<String> = emptyList(),
        apiKey: String
    ): RouteDirectionsResult?
}

package com.juko.app.core.location

actual class GooglePlacesDataSource actual constructor() {
    actual suspend fun fetchPredictions(
        query: String,
        apiKey: String,
        sessionToken: String
    ): List<PlaceSuggestion> = emptyList()

    actual suspend fun fetchPlaceDetails(
        placeId: String,
        apiKey: String,
        sessionToken: String
    ): PlaceLocation? = null

    actual suspend fun calculateRouteDirections(
        origin: String,
        destination: String,
        waypoints: List<String>,
        apiKey: String
    ): RouteDirectionsResult? = null
}

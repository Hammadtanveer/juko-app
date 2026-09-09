package com.juko.app.core.location

import com.juko.app.core.config.AppConfig
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

/**
 * Place suggestion item returned by autocomplete queries.
 */
data class PlaceSuggestion(
    val placeId: String,
    val primaryText: String,
    val secondaryText: String = "",
    val fullText: String = if (secondaryText.isNotBlank()) "$primaryText, $secondaryText" else primaryText,
    val latitude: Double? = null,
    val longitude: Double? = null
)

/**
 * Confirmed place location model representing a validated geographic point.
 */
data class PlaceLocation(
    val name: String,
    val placeId: String? = null,
    val secondaryText: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isConfirmed: Boolean = false
) {
    val displayName: String
        get() = name
}

/**
 * Clean abstraction for Location and Google Places Autocomplete.
 * Can be swapped with the official Google Places SDK/REST API by providing an alternative implementation.
 */
interface PlacesAutocompleteService {
    suspend fun searchPlaces(query: String): List<PlaceSuggestion>
    suspend fun getPlaceDetails(placeId: String): PlaceLocation?
    suspend fun calculateRouteDurationMinutes(
        origin: String,
        destination: String,
        originLoc: PlaceLocation? = null,
        destinationLoc: PlaceLocation? = null,
        pickupPoints: List<PlaceLocation> = emptyList()
    ): Int
}


/**
 * Default implementation of PlacesAutocompleteService.
 * Connects directly to Google Places API when configured via local.properties / AppConfig,
 * and seamlessly falls back to a rich curated local database when offline or unconfigured.
 */
class DefaultPlacesAutocompleteService(
    private val dataSource: GooglePlacesDataSource = GooglePlacesDataSource()
) : PlacesAutocompleteService {

    private var currentSessionToken: String = generateSessionToken()

    private fun generateSessionToken(): String {
        return "sess_${Clock.System.now().toEpochMilliseconds()}_${(1000..9999).random()}"
    }

    // Preloaded geographic database for instant, reliable suggestions and offline fallback
    private val localPlacesDatabase = listOf(
        PlaceSuggestion("place_seo_01", "Seohara", "Bijnor, Uttar Pradesh, India", latitude = 29.2135, longitude = 78.5835),
        PlaceSuggestion("place_noo_01", "Noorpur", "Bijnor, Uttar Pradesh, India", latitude = 29.1500, longitude = 78.4100),
        PlaceSuggestion("place_cha_01", "Chandpur", "Bijnor, Uttar Pradesh, India", latitude = 29.1367, longitude = 78.2736),
        PlaceSuggestion("place_bij_01", "Bijnor", "Uttar Pradesh, India", latitude = 29.3724, longitude = 78.1358),
        PlaceSuggestion("place_del_01", "Delhi", "National Capital Territory of Delhi, India", latitude = 28.6139, longitude = 77.2090),
        PlaceSuggestion("place_del_02", "New Delhi Railway Station", "Bhavbhuti Marg, Ratan Lal Market, Delhi", latitude = 28.6430, longitude = 77.2194),
        PlaceSuggestion("place_del_03", "Anand Vihar ISBT", "Anand Vihar, Delhi", latitude = 28.6469, longitude = 77.3164),
        PlaceSuggestion("place_del_04", "Kashmere Gate ISBT", "Kashmere Gate, Delhi", latitude = 28.6675, longitude = 77.2285),
        PlaceSuggestion("place_mor_01", "Moradabad", "Uttar Pradesh, India", latitude = 28.8386, longitude = 78.7733),
        PlaceSuggestion("place_mee_01", "Meerut", "Uttar Pradesh, India", latitude = 28.9845, longitude = 77.7064),
        PlaceSuggestion("place_har_01", "Haridwar", "Uttarakhand, India", latitude = 29.9457, longitude = 78.1642),
        PlaceSuggestion("place_deh_01", "Dehradun", "Uttarakhand, India", latitude = 30.3165, longitude = 78.0322),
        PlaceSuggestion("place_noi_01", "Noida", "Gautam Buddha Nagar, Uttar Pradesh, India", latitude = 28.5355, longitude = 77.3910),
        PlaceSuggestion("place_noi_02", "Greater Noida", "Uttar Pradesh, India", latitude = 28.4744, longitude = 77.5040),
        PlaceSuggestion("place_gur_01", "Gurugram", "Haryana, India", latitude = 28.4595, longitude = 77.0266),
        PlaceSuggestion("place_mum_01", "Mumbai", "Maharashtra, India", latitude = 19.0760, longitude = 72.8777),
        PlaceSuggestion("place_pun_01", "Pune", "Maharashtra, India", latitude = 18.5204, longitude = 73.8567),
        PlaceSuggestion("place_lon_01", "Lonavala", "Pune, Maharashtra, India", latitude = 18.7557, longitude = 73.4091),
        PlaceSuggestion("place_jai_01", "Jaipur", "Rajasthan, India", latitude = 26.9124, longitude = 75.7873),
        PlaceSuggestion("place_agr_01", "Agra", "Uttar Pradesh, India", latitude = 27.1767, longitude = 78.0081),
        PlaceSuggestion("place_luc_01", "Lucknow", "Uttar Pradesh, India", latitude = 26.8467, longitude = 80.9462),
        PlaceSuggestion("place_kan_01", "Kanpur", "Uttar Pradesh, India", latitude = 26.4499, longitude = 80.3319)
    )

    override suspend fun searchPlaces(query: String): List<PlaceSuggestion> {
        val trimmed = query.trim()
        if (trimmed.length < 2) return emptyList()

        // 1. If Google Places API is configured, call Google Places Autocomplete
        if (AppConfig.isGooglePlacesConfigured) {
            val remoteResults = dataSource.fetchPredictions(
                query = trimmed,
                apiKey = AppConfig.googlePlacesApiKey,
                sessionToken = currentSessionToken
            )
            if (remoteResults.isNotEmpty()) {
                return remoteResults
            }
        }

        // 2. Fallback to local curated database
        delay(100)
        return localPlacesDatabase.filter { place ->
            place.primaryText.contains(trimmed, ignoreCase = true) ||
            place.secondaryText.contains(trimmed, ignoreCase = true) ||
            place.fullText.contains(trimmed, ignoreCase = true)
        }.sortedByDescending { place ->
            if (place.primaryText.startsWith(trimmed, ignoreCase = true)) 2 else 1
        }
    }

    override suspend fun getPlaceDetails(placeId: String): PlaceLocation? {
        // 1. If Google Places API is configured and placeId is from Google, fetch place details
        if (AppConfig.isGooglePlacesConfigured && !placeId.startsWith("place_")) {
            val remoteDetails = dataSource.fetchPlaceDetails(
                placeId = placeId,
                apiKey = AppConfig.googlePlacesApiKey,
                sessionToken = currentSessionToken
            )
            if (remoteDetails != null) {
                // Refresh session token for subsequent searches
                currentSessionToken = generateSessionToken()
                return remoteDetails
            }
        }

        // 2. Fallback to local database
        val matched = localPlacesDatabase.find { it.placeId == placeId } ?: return null
        return PlaceLocation(
            name = matched.primaryText,
            placeId = matched.placeId,
            secondaryText = matched.secondaryText,
            latitude = matched.latitude,
            longitude = matched.longitude,
            isConfirmed = true
        )
    }

    override suspend fun calculateRouteDurationMinutes(
        origin: String,
        destination: String,
        originLoc: PlaceLocation?,
        destinationLoc: PlaceLocation?,
        pickupPoints: List<PlaceLocation>
    ): Int {
        // 1. Try Google Directions API if configured
        if (AppConfig.isGooglePlacesConfigured && origin.isNotBlank() && destination.isNotBlank()) {
            val originQuery = if (originLoc?.latitude != null && originLoc.longitude != null) {
                "${originLoc.latitude},${originLoc.longitude}"
            } else origin
            val destQuery = if (destinationLoc?.latitude != null && destinationLoc.longitude != null) {
                "${destinationLoc.latitude},${destinationLoc.longitude}"
            } else destination
            val waypoints = pickupPoints.mapNotNull {
                if (it.latitude != null && it.longitude != null) "${it.latitude},${it.longitude}"
                else it.name.takeIf { n -> n.isNotBlank() }
            }

            val result = dataSource.calculateRouteDirections(
                origin = originQuery,
                destination = destQuery,
                waypoints = waypoints,
                apiKey = AppConfig.googlePlacesApiKey
            )
            if (result != null && result.durationSeconds > 0) {
                return (result.durationSeconds / 60).toInt()
            }
        }

        // 2. Distance-based fallback calculation using coordinate geometry
        val validPoints = mutableListOf<Pair<Double, Double>>()
        val originLat = originLoc?.latitude ?: findCoord(origin)?.first
        val originLon = originLoc?.longitude ?: findCoord(origin)?.second
        if (originLat != null && originLon != null) validPoints.add(originLat to originLon)

        for (p in pickupPoints) {
            val pLat = p.latitude ?: findCoord(p.name)?.first
            val pLon = p.longitude ?: findCoord(p.name)?.second
            if (pLat != null && pLon != null) validPoints.add(pLat to pLon)
        }

        val destLat = destinationLoc?.latitude ?: findCoord(destination)?.first
        val destLon = destinationLoc?.longitude ?: findCoord(destination)?.second
        if (destLat != null && destLon != null) validPoints.add(destLat to destLon)

        if (validPoints.size >= 2) {
            var totalAerialKm = 0.0
            for (i in 0 until validPoints.size - 1) {
                val p1 = validPoints[i]
                val p2 = validPoints[i + 1]
                totalAerialKm += haversineDistanceKm(p1.first, p1.second, p2.first, p2.second)
            }
            // Road winding factor ~1.25x, average driving speed ~45 km/h
            val roadKm = totalAerialKm * 1.25
            val drivingMins = (roadKm / 45.0 * 60.0).toInt()
            val stopBufferMins = (pickupPoints.filter { it.name.isNotBlank() }.size) * 10
            return (drivingMins + stopBufferMins).coerceAtLeast(30)
        }

        // 3. Fallback based on stop count
        val stopsCount = pickupPoints.filter { it.name.isNotBlank() }.size
        return 180 + (stopsCount * 45)
    }

    private fun findCoord(name: String): Pair<Double, Double>? {
        if (name.isBlank()) return null
        val match = localPlacesDatabase.find {
            it.primaryText.equals(name, ignoreCase = true) || it.fullText.contains(name, ignoreCase = true)
        }
        return if (match?.latitude != null && match.longitude != null) {
            match.latitude to match.longitude
        } else null
    }

    private fun haversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = (lat2 - lat1) * (kotlin.math.PI / 180.0)
        val dLon = (lon2 - lon1) * (kotlin.math.PI / 180.0)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(lat1 * (kotlin.math.PI / 180.0)) * kotlin.math.cos(lat2 * (kotlin.math.PI / 180.0)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return r * c
    }
}

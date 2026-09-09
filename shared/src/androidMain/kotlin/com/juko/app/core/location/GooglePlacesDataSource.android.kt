package com.juko.app.core.location

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

actual class GooglePlacesDataSource actual constructor() {

    actual suspend fun fetchPredictions(
        query: String,
        apiKey: String,
        sessionToken: String
    ): List<PlaceSuggestion> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json" +
                    "?input=$encodedQuery" +
                    "&components=country:in" +
                    "&sessiontoken=$sessionToken" +
                    "&key=$apiKey"

            val jsonStr = httpGet(urlString) ?: return@withContext emptyList()
            val json = JSONObject(jsonStr)
            val status = json.optString("status")

            if (status != "OK" && status != "ZERO_RESULTS") {
                return@withContext emptyList()
            }

            val predictions = json.optJSONArray("predictions") ?: return@withContext emptyList()
            val list = mutableListOf<PlaceSuggestion>()

            for (i in 0 until predictions.length()) {
                val item = predictions.getJSONObject(i)
                val placeId = item.optString("place_id")
                val structured = item.optJSONObject("structured_formatting")
                val mainText = structured?.optString("main_text")?.takeIf { it.isNotBlank() }
                    ?: item.optString("description")
                val secondaryText = structured?.optString("secondary_text") ?: ""

                list.add(
                    PlaceSuggestion(
                        placeId = placeId,
                        primaryText = mainText,
                        secondaryText = secondaryText
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    actual suspend fun fetchPlaceDetails(
        placeId: String,
        apiKey: String,
        sessionToken: String
    ): PlaceLocation? = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://maps.googleapis.com/maps/api/place/details/json" +
                    "?place_id=$placeId" +
                    "&fields=geometry,name,formatted_address" +
                    "&sessiontoken=$sessionToken" +
                    "&key=$apiKey"

            val jsonStr = httpGet(urlString) ?: return@withContext null
            val json = JSONObject(jsonStr)
            val status = json.optString("status")

            if (status != "OK") return@withContext null

            val result = json.optJSONObject("result") ?: return@withContext null
            val name = result.optString("name")
            val formattedAddress = result.optString("formatted_address")
            val geometry = result.optJSONObject("geometry")
            val location = geometry?.optJSONObject("location")
            val lat = location?.optDouble("lat")
            val lng = location?.optDouble("lng")

            PlaceLocation(
                name = name,
                placeId = placeId,
                secondaryText = formattedAddress,
                latitude = lat,
                longitude = lng,
                isConfirmed = true
            )
        } catch (e: Exception) {
            null
        }
    }

    actual suspend fun calculateRouteDirections(
        origin: String,
        destination: String,
        waypoints: List<String>,
        apiKey: String
    ): RouteDirectionsResult? = withContext(Dispatchers.IO) {
        try {
            val encodedOrigin = URLEncoder.encode(origin, "UTF-8")
            val encodedDest = URLEncoder.encode(destination, "UTF-8")
            val waypointsParam = if (waypoints.isNotEmpty()) {
                "&waypoints=" + URLEncoder.encode(waypoints.joinToString("|"), "UTF-8")
            } else ""

            val urlString = "https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin=$encodedOrigin" +
                    "&destination=$encodedDest" +
                    waypointsParam +
                    "&key=$apiKey"

            val jsonStr = httpGet(urlString) ?: return@withContext null
            val json = JSONObject(jsonStr)
            val status = json.optString("status")
            if (status != "OK") return@withContext null

            val routes = json.optJSONArray("routes") ?: return@withContext null
            if (routes.length() == 0) return@withContext null
            val route = routes.getJSONObject(0)
            val legs = route.optJSONArray("legs") ?: return@withContext null

            var totalSeconds = 0L
            var totalMeters = 0L
            for (i in 0 until legs.length()) {
                val leg = legs.getJSONObject(i)
                totalSeconds += leg.optJSONObject("duration")?.optLong("value") ?: 0L
                totalMeters += leg.optJSONObject("distance")?.optLong("value") ?: 0L
            }

            val hours = totalSeconds / 3600
            val mins = (totalSeconds % 3600) / 60
            val durationText = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

            RouteDirectionsResult(
                durationSeconds = totalSeconds,
                distanceMeters = totalMeters,
                durationText = durationText
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun httpGet(urlString: String): String? {
        return try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 6000
            conn.readTimeout = 6000
            if (conn.responseCode in 200..299) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

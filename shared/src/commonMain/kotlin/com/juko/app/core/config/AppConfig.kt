package com.juko.app.core.config

/**
 * Global application configuration.
 * The [googlePlacesApiKey] can be provided via local.properties (GOOGLE_PLACES_API_KEY)
 * or set at runtime.
 */
object AppConfig {
    /**
     * Google Places API Key.
     * Initialized from BuildConfig on Android or can be set programmatically.
     * Default placeholder: "YOUR_GOOGLE_PLACES_API_KEY_HERE"
     */
    var googlePlacesApiKey: String = "YOUR_GOOGLE_PLACES_API_KEY_HERE"

    /**
     * Determines whether a real Google Places API key has been supplied.
     */
    val isGooglePlacesConfigured: Boolean
        get() = googlePlacesApiKey.isNotBlank() &&
                googlePlacesApiKey != "YOUR_GOOGLE_PLACES_API_KEY_HERE" &&
                !googlePlacesApiKey.startsWith("YOUR_")
}

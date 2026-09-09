package com.example.juko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Inject Google Places API Key from BuildConfig / local.properties
        com.juko.app.core.config.AppConfig.googlePlacesApiKey = BuildConfig.GOOGLE_PLACES_API_KEY
        com.juko.app.core.storage.AndroidPlatformContext.context = applicationContext
        com.juko.app.core.data.RecentSearchesManager.loadInitialSearches()

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
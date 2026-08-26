package com.example.juko

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.juko.app.core.data.TokenManager
import com.juko.app.core.presentation.theme.JukoTheme
import com.juko.app.feature.auth.presentation.auth.AuthScreen
import com.juko.app.feature.home.presentation.HomeScreen
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val tokenManager = koinInject<TokenManager>()
    var isAuthenticated by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        val token = tokenManager.getAccessToken()
        isAuthenticated = !token.isNullOrBlank()
    }

    JukoTheme {
        isAuthenticated?.let { authenticated ->
            Navigator(screen = if (authenticated) HomeScreen() else AuthScreen())
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "JUKO",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

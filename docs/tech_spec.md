# 🚗 JUKO — Frontend Technical Specification

> **Version:** 1.0  
> **Date:** August 24, 2026  
> **Framework:** Kotlin Multiplatform + Compose Multiplatform  
> **Targets:** Android & iOS  
> **Design System:** Velocity Drive  
> **Backend:** External API (provided by backend team)

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Tech Stack](#tech-stack)
3. [Gradle Module Structure](#gradle-module-structure)
4. [Project Folder Structure](#project-folder-structure)
5. [Design System Implementation](#design-system-implementation)
6. [Feature Modules](#feature-modules)
7. [State Management (MVVM + UDF)](#state-management)
8. [Navigation Architecture](#navigation-architecture)
9. [Network Layer](#network-layer)
10. [WebSocket Layer](#websocket-layer)
11. [Local Storage & Offline](#local-storage--offline)
12. [Dependency Injection](#dependency-injection)
13. [Platform-Specific Code](#platform-specific-code)
14. [Image Loading & Media](#image-loading--media)
15. [Security](#security)
16. [Performance Optimization](#performance-optimization)
17. [Testing Strategy](#testing-strategy)
18. [Build & Release](#build--release)
19. [Dependency Versions](#dependency-versions)

---

## Architecture Overview

```
┌────────────────────────────────────────────────────────────────┐
│                    JUKO KMP Application                        │
│                                                                │
│  ┌──────────────┐              ┌──────────────┐                │
│  │  androidApp  │              │    iosApp     │                │
│  │  (Kotlin)    │              │   (Swift)     │                │
│  │  MainActivity│              │  ContentView  │                │
│  └──────┬───────┘              └──────┬────────┘                │
│         │                             │                         │
│         └──────────────┬──────────────┘                         │
│                        ▼                                        │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                  :shared (commonMain)                    │    │
│  │                                                         │    │
│  │   ┌─────────────────────────────────────────────────┐   │    │
│  │   │              PRESENTATION LAYER                 │   │    │
│  │   │   Compose UI  ←→  ViewModel  ←→  UiState       │   │    │
│  │   └─────────────────────┬───────────────────────────┘   │    │
│  │                         │                               │    │
│  │   ┌─────────────────────┴───────────────────────────┐   │    │
│  │   │                DOMAIN LAYER                     │   │    │
│  │   │   UseCases  ←→  Repository Interfaces  ←→ Models│   │    │
│  │   └─────────────────────┬───────────────────────────┘   │    │
│  │                         │                               │    │
│  │   ┌─────────────────────┴───────────────────────────┐   │    │
│  │   │                 DATA LAYER                      │   │    │
│  │   │   RepositoryImpl  ←→  ApiService  ←→  LocalDB  │   │    │
│  │   │                       (Ktor)        (SQLDelight)│   │    │
│  │   └─────────────────────────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                │
└────────────────────────────────────────────────────────────────┘
                        │ HTTPS / WSS
                        ▼
              ┌─────────────────────┐
              │   External APIs     │
              │ (Backend Team)      │
              └─────────────────────┘
```

### Pattern: Clean Architecture + MVVM + UDF

```
User Action → ViewModel → UseCase → Repository → ApiService → Backend API
                                                      ↓
UI ← State ← ViewModel ← UseCase ← Repository ← Response
```

- **Presentation:** Compose screens + ViewModels with `StateFlow`
- **Domain:** UseCases + Repository interfaces + domain models (pure Kotlin, no dependencies)
- **Data:** Repository implementations + Ktor API calls + SQLDelight local DB + DTOs

---

## Tech Stack

| Category | Library | Version | Purpose |
|---|---|---|---|
| **Language** | Kotlin | 2.1.0 | Shared business logic & UI |
| **UI Framework** | Compose Multiplatform | 1.7.0 | Shared UI for Android + iOS |
| **Navigation** | Voyager | 1.1.0 | Type-safe multiplatform navigation + bottom tabs |
| **HTTP Client** | Ktor Client | 3.0.0 | REST API calls + WebSocket |
| **Serialization** | kotlinx.serialization | 1.7.0 | JSON parsing |
| **DI** | Koin | 4.0.0 | Multiplatform dependency injection |
| **Image Loading** | Coil 3 | 3.0.0 | Async images + disk/memory cache |
| **Local Database** | SQLDelight | 2.0.2 | Offline cache + local persistence |
| **Key-Value Store** | multiplatform-settings | 1.2.0 | Token storage, preferences, flags |
| **Async/Reactive** | kotlinx.coroutines | 1.9.0 | Coroutines + Flow |
| **Date/Time** | kotlinx-datetime | 0.6.0 | Multiplatform date handling |
| **Logging** | Napier | 2.7.1 | Debug + production logging |
| **Maps (Android)** | Google Maps Compose | 6.2.0 | Route display, pins |
| **Maps (iOS)** | Apple MapKit (via expect/actual) | Native | Route display, pins |
| **Permissions** | Moko Permissions | 0.18.0 | Camera, location, storage permissions |
| **File Picker** | Moko Media / CMP File Picker | Latest | Image/document picker |
| **Build System** | Gradle (KTS) | 8.9 | Multi-module builds |
| **Analytics** | Firebase KMP SDK | Latest | Event tracking |
| **Crash Reporting** | Firebase Crashlytics | Latest | Crash logs |
| **Push** | Firebase Messaging | Latest | FCM (Android) + APNs bridge (iOS) |

---

## Gradle Module Structure

```
juko/
├── build.gradle.kts              # Root: plugins, repos
├── settings.gradle.kts           # Module declarations
├── gradle/
│   └── libs.versions.toml        # Version catalog
│
├── :shared                       # KMP shared module (ALL code lives here)
│   ├── commonMain                # Shared code (95%+ of codebase)
│   ├── androidMain               # Android-specific (Maps, Keystore, etc.)
│   └── iosMain                   # iOS-specific (MapKit, Keychain, etc.)
│
├── :androidApp                   # Android entry point (thin shell)
│   └── MainActivity.kt
│
└── :iosApp                       # iOS entry point (thin shell)
    └── ContentView.swift
```

### Gradle Configuration

```kotlin
// shared/build.gradle.kts
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget()
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)

            // Navigation
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.tabNavigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.transitions)

            // Networking
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.json)

            // DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            // Local
            implementation(libs.sqldelight.coroutines)
            implementation(libs.multiplatformSettings)

            // Utility
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.napier)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android)
            implementation(libs.koin.android)
            implementation(libs.google.maps.compose)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native)
        }
    }
}
```

---

## Project Folder Structure

```
shared/src/commonMain/kotlin/com/juko/

├── App.kt                                   # Root @Composable
│
├── core/
│   ├── di/
│   │   ├── AppModule.kt                     # Root Koin module
│   │   ├── NetworkModule.kt                 # Ktor client, ApiService
│   │   ├── DatabaseModule.kt                # SQLDelight driver
│   │   └── FeatureModules.kt                # Per-feature DI
│   │
│   ├── network/
│   │   ├── ApiClient.kt                     # Ktor HttpClient factory
│   │   ├── ApiRoutes.kt                     # All endpoint URL constants
│   │   ├── AuthInterceptor.kt              # Auto-inject JWT, handle 401 refresh
│   │   ├── NetworkResult.kt                 # sealed class Success/Error/Loading
│   │   ├── ErrorResponse.kt                # Standard API error DTO
│   │   └── WebSocketManager.kt             # Persistent WS connection manager
│   │
│   ├── storage/
│   │   ├── TokenManager.kt                  # Save/read/clear access + refresh tokens
│   │   ├── PreferencesManager.kt            # Onboarding flags, settings
│   │   └── SessionManager.kt               # Login state, current user cache
│   │
│   ├── ui/
│   │   ├── theme/
│   │   │   ├── JukoTheme.kt                # MaterialTheme wrapper
│   │   │   ├── Color.kt                    # Velocity Drive color tokens
│   │   │   ├── Typography.kt               # Inter type scale (8 styles)
│   │   │   ├── Spacing.kt                  # 4px grid spacing tokens
│   │   │   └── Shape.kt                    # Border radius tokens
│   │   │
│   │   └── components/                      # Reusable composables
│   │       ├── JukoButton.kt               # Primary, Success, Ghost variants
│   │       ├── JukoCard.kt                 # Elevated card with shadow
│   │       ├── JukoTextField.kt            # Styled input with label-caps
│   │       ├── JukoTopBar.kt               # App bar with back navigation
│   │       ├── JukoBottomNav.kt            # 4-tab bottom navigation
│   │       ├── JukoSegmentedControl.kt     # Pill-style tab switcher
│   │       ├── JukoEmptyState.kt           # Reusable empty state (icon + text)
│   │       ├── JukoLoadingState.kt         # Skeleton / shimmer loading
│   │       ├── JukoErrorState.kt           # Error with retry button
│   │       ├── JukoAvatar.kt              # Circular profile image
│   │       ├── JukoRatingBar.kt           # Star rating display
│   │       ├── JukoBadge.kt               # Notification count badge
│   │       └── JukoChip.kt               # Status chips (Scheduled, Active, etc.)
│   │
│   └── util/
│       ├── Validators.kt                    # Email, phone, OTP, plate validators
│       ├── DateTimeFormatter.kt             # "2h ago", "Aug 24", "10:30 AM"
│       ├── CurrencyFormatter.kt             # ₹450, ₹1,200
│       ├── Extensions.kt                   # Flow, Modifier, String extensions
│       └── Constants.kt                     # App-wide constants
│
├── feature/
│   │
│   ├── auth/
│   │   ├── data/
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequest.kt          # { email, password }
│   │   │   │   ├── SignupRequest.kt          # { name, email, phone, password }
│   │   │   │   ├── OtpRequest.kt             # { phone, otp }
│   │   │   │   └── AuthResponse.kt           # { access_token, refresh_token, user }
│   │   │   ├── remote/
│   │   │   │   └── AuthApiService.kt         # Ktor calls to /auth/*
│   │   │   ├── mapper/
│   │   │   │   └── AuthMapper.kt             # DTO → Domain model
│   │   │   └── repository/
│   │   │       └── AuthRepositoryImpl.kt
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   └── User.kt                  # Domain user model
│   │   │   ├── repository/
│   │   │   │   └── AuthRepository.kt         # Interface
│   │   │   └── usecase/
│   │   │       ├── LoginUseCase.kt
│   │   │       ├── SignupUseCase.kt
│   │   │       ├── SendOtpUseCase.kt
│   │   │       ├── VerifyOtpUseCase.kt
│   │   │       └── ResetPasswordUseCase.kt
│   │   └── presentation/
│   │       ├── login/
│   │       │   ├── LoginScreen.kt
│   │       │   ├── LoginViewModel.kt
│   │       │   └── LoginState.kt
│   │       ├── signup/
│   │       │   ├── SignupScreen.kt
│   │       │   ├── SignupViewModel.kt
│   │       │   └── SignupState.kt
│   │       └── forgot_password/
│   │           ├── ForgotPasswordScreen.kt
│   │           ├── OtpVerifyScreen.kt
│   │           ├── ResetPasswordScreen.kt
│   │           ├── ForgotPasswordViewModel.kt
│   │           └── ForgotPasswordState.kt
│   │
│   ├── home/
│   │   ├── data/
│   │   │   ├── dto/
│   │   │   ├── remote/HomeApiService.kt
│   │   │   └── repository/HomeRepositoryImpl.kt
│   │   ├── domain/
│   │   │   ├── model/RideSummary.kt
│   │   │   ├── repository/HomeRepository.kt
│   │   │   └── usecase/SearchRidesUseCase.kt
│   │   └── presentation/
│   │       ├── HomeScreen.kt
│   │       ├── HomeViewModel.kt
│   │       └── HomeState.kt
│   │
│   ├── rides/
│   │   ├── data/
│   │   │   ├── dto/
│   │   │   │   ├── RideDto.kt
│   │   │   │   ├── BookingDto.kt
│   │   │   │   └── RideHistoryDto.kt
│   │   │   ├── remote/RidesApiService.kt
│   │   │   ├── mapper/RideMapper.kt
│   │   │   └── repository/RidesRepositoryImpl.kt
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Ride.kt
│   │   │   │   ├── Booking.kt
│   │   │   │   └── RideHistory.kt
│   │   │   ├── repository/RidesRepository.kt
│   │   │   └── usecase/
│   │   │       ├── GetMyRidesUseCase.kt
│   │   │       ├── GetBookingRequestsUseCase.kt
│   │   │       ├── AcceptBookingUseCase.kt
│   │   │       ├── DeclineBookingUseCase.kt
│   │   │       └── GetRideHistoryUseCase.kt
│   │   └── presentation/
│   │       ├── my_rides/
│   │       │   ├── MyRidesScreen.kt
│   │       │   ├── MyRidesViewModel.kt
│   │       │   └── MyRidesState.kt
│   │       ├── booking_requests/
│   │       │   ├── BookingRequestsScreen.kt
│   │       │   ├── BookingRequestsViewModel.kt
│   │       │   └── BookingRequestsState.kt
│   │       └── history/
│   │           ├── HistoryScreen.kt
│   │           ├── HistoryViewModel.kt
│   │           └── HistoryState.kt
│   │
│   ├── post_ride/
│   │   ├── data/
│   │   │   ├── dto/PostRideRequest.kt
│   │   │   ├── remote/PostRideApiService.kt
│   │   │   └── repository/PostRideRepositoryImpl.kt
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── RideDetails.kt
│   │   │   │   ├── RouteInfo.kt
│   │   │   │   └── RidePreferences.kt
│   │   │   ├── repository/PostRideRepository.kt
│   │   │   └── usecase/
│   │   │       ├── PublishRideUseCase.kt
│   │   │       └── ValidateRideUseCase.kt
│   │   └── presentation/
│   │       ├── ride_details/
│   │       │   ├── RideDetailsScreen.kt
│   │       │   ├── RideDetailsViewModel.kt
│   │       │   └── RideDetailsState.kt
│   │       └── route_pricing/
│   │           ├── RoutePricingScreen.kt
│   │           ├── RoutePricingViewModel.kt
│   │           └── RoutePricingState.kt
│   │
│   ├── chat/
│   │   ├── data/
│   │   │   ├── dto/
│   │   │   │   ├── ConversationDto.kt
│   │   │   │   ├── MessageDto.kt
│   │   │   │   └── WebSocketMessage.kt
│   │   │   ├── remote/
│   │   │   │   ├── ChatApiService.kt        # REST fallback
│   │   │   │   └── ChatWebSocketService.kt  # Real-time via WS
│   │   │   ├── mapper/ChatMapper.kt
│   │   │   └── repository/ChatRepositoryImpl.kt
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Conversation.kt
│   │   │   │   └── Message.kt
│   │   │   ├── repository/ChatRepository.kt
│   │   │   └── usecase/
│   │   │       ├── GetConversationsUseCase.kt
│   │   │       ├── GetMessagesUseCase.kt
│   │   │       ├── SendMessageUseCase.kt
│   │   │       └── MarkAsReadUseCase.kt
│   │   └── presentation/
│   │       ├── inbox/
│   │       │   ├── InboxScreen.kt
│   │       │   ├── InboxViewModel.kt
│   │       │   └── InboxState.kt
│   │       └── conversation/
│   │           ├── ChatScreen.kt
│   │           ├── ChatViewModel.kt
│   │           └── ChatState.kt
│   │
│   ├── notifications/
│   │   ├── data/
│   │   │   ├── dto/NotificationDto.kt
│   │   │   ├── remote/NotificationApiService.kt
│   │   │   └── repository/NotificationRepositoryImpl.kt
│   │   ├── domain/
│   │   │   ├── model/AppNotification.kt
│   │   │   ├── repository/NotificationRepository.kt
│   │   │   └── usecase/
│   │   │       ├── GetNotificationsUseCase.kt
│   │   │       └── MarkReadUseCase.kt
│   │   └── presentation/
│   │       ├── NotificationsScreen.kt
│   │       ├── NotificationsViewModel.kt
│   │       └── NotificationsState.kt
│   │
│   └── profile/
│       ├── data/
│       │   ├── dto/
│       │   │   ├── ProfileDto.kt
│       │   │   ├── VehicleDto.kt
│       │   │   └── DocumentUploadResponse.kt
│       │   ├── remote/
│       │   │   ├── ProfileApiService.kt
│       │   │   └── VehicleApiService.kt
│       │   └── repository/
│       │       ├── ProfileRepositoryImpl.kt
│       │       └── VehicleRepositoryImpl.kt
│       ├── domain/
│       │   ├── model/
│       │   │   ├── DriverProfile.kt
│       │   │   ├── Vehicle.kt
│       │   │   └── DriverDocument.kt
│       │   ├── repository/
│       │   │   ├── ProfileRepository.kt
│       │   │   └── VehicleRepository.kt
│       │   └── usecase/
│       │       ├── GetProfileUseCase.kt
│       │       ├── UpdateProfileUseCase.kt
│       │       ├── UploadDocumentUseCase.kt
│       │       ├── AddVehicleUseCase.kt
│       │       └── GetVehiclesUseCase.kt
│       └── presentation/
│           ├── driver_profile/
│           │   ├── DriverProfileScreen.kt
│           │   ├── LicenceVerificationScreen.kt
│           │   ├── DriverProfileViewModel.kt
│           │   └── DriverProfileState.kt
│           └── add_vehicle/
│               ├── AddVehicleScreen.kt
│               ├── AddVehicleViewModel.kt
│               └── AddVehicleState.kt
│
└── navigation/
    ├── AppNavigator.kt               # Root navigator (auth check → route)
    ├── AuthNavigator.kt              # Login/Signup/ForgotPassword flow
    ├── MainNavigator.kt              # Bottom nav tabs container
    ├── Screens.kt                    # All screen route sealed classes
    └── BottomNavTab.kt               # Tab definitions with icons
```

---

## Design System Implementation

### JukoTheme.kt

```kotlin
@Composable
fun JukoTheme(
    darkTheme: Boolean = false, // Light only for v1
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JukoLightColorScheme,
        typography = JukoTypography,
        shapes = JukoShapes,
        content = content
    )
}

// Spacing tokens accessible via LocalComposition
val LocalSpacing = staticCompositionLocalOf { JukoSpacing() }

data class JukoSpacing(
    val base: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val edgeMargin: Dp = 16.dp,
    val stackGap: Dp = 12.dp
)
```

### Color.kt

```kotlin
val JukoLightColorScheme = lightColorScheme(
    primary = Color(0xFF003D9B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF0052CC),
    onPrimaryContainer = Color(0xFFC4D2FF),
    secondary = Color(0xFF5D5F5F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDFE0E0),
    tertiary = Color(0xFF004E32),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF006844),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    surface = Color(0xFFF9F9FF),
    onSurface = Color(0xFF041B3C),
    onSurfaceVariant = Color(0xFF434654),
    surfaceContainer = Color(0xFFE8EDFF),
    surfaceContainerHigh = Color(0xFFE0E8FF),
    surfaceContainerHighest = Color(0xFFD7E2FF),
    outline = Color(0xFF737685),
    outlineVariant = Color(0xFFC3C6D6),
    inverseSurface = Color(0xFF1D3052),
    inverseOnSurface = Color(0xFFEDF0FF),
    inversePrimary = Color(0xFFB2C5FF),
    background = Color(0xFFF9F9FF),
    onBackground = Color(0xFF041B3C),
)

// Brand colors outside Material tokens
object JukoBrandColors {
    val confidentBlue = Color(0xFF0052CC)
    val successGreen = Color(0xFF36B37E)
    val navy = Color(0xFF172B4D)
    val surfaceGrey = Color(0xFFF4F5F7)
}
```

### Typography.kt

```kotlin
val JukoTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp,
        letterSpacing = (-0.02).em
    ),
    headlineMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 32.sp,
        letterSpacing = (-0.01).em
    ),
    titleSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 16.sp,
        letterSpacing = 0.05.em
    )
)

// Custom text styles outside Material
object JukoTextStyles {
    val headlineMdMobile = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp
    )
    val numericData = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 32.sp,
        letterSpacing = (-0.02).em
    )
}
```

### Shape.kt

```kotlin
val JukoShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),    // sm: 0.25rem
    small = RoundedCornerShape(8.dp),         // DEFAULT: 0.5rem
    medium = RoundedCornerShape(12.dp),       // md: 0.75rem
    large = RoundedCornerShape(16.dp),        // lg: 1rem
    extraLarge = RoundedCornerShape(24.dp)    // xl: 1.5rem
)

val PillShape = RoundedCornerShape(percent = 50)  // full: 9999px
```

---

## Feature Modules

| Module | Screens | ViewModel | API Calls | Local Cache |
|---|---|---|---|---|
| **auth** | Login, Signup, ForgotPassword, OtpVerify | 3 VMs | `/auth/*` | Token storage |
| **home** | RideSearchHome | 1 VM | `/rides/search` | Recent searches |
| **rides** | MyRides, BookingRequests, History | 3 VMs | `/rides/*`, `/bookings/*` | Rides list cache |
| **post_ride** | RideDetails, RoutePricing | 2 VMs (shared state) | `POST /rides` | Draft ride |
| **chat** | Inbox, Chat | 2 VMs | `/conversations/*`, WebSocket | Messages cache |
| **notifications** | Notifications | 1 VM | `/notifications/*`, WebSocket | Notifications cache |
| **profile** | DriverProfile, LicenceVerify, AddVehicle | 2 VMs | `/users/*`, `/vehicles/*` | Profile cache |

---

## State Management

### MVVM + Unidirectional Data Flow (UDF)

Every screen follows this pattern:

```kotlin
// 1. STATE — Immutable data class
data class MyRidesState(
    val rides: List<Ride> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedTab: RideTab = RideTab.UPCOMING
)

enum class RideTab { UPCOMING, ACTIVE, COMPLETED }

// 2. EVENTS — Sealed interface for user actions
sealed interface MyRidesEvent {
    data object LoadRides : MyRidesEvent
    data object Refresh : MyRidesEvent
    data class TabSelected(val tab: RideTab) : MyRidesEvent
    data class RideClicked(val rideId: String) : MyRidesEvent
}

// 3. SIDE EFFECTS — One-time events (navigation, snackbar)
sealed interface MyRidesSideEffect {
    data class NavigateToBooking(val rideId: String) : MyRidesSideEffect
    data class ShowError(val message: String) : MyRidesSideEffect
}

// 4. VIEWMODEL
class MyRidesViewModel(
    private val getMyRidesUseCase: GetMyRidesUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(MyRidesState())
    val state: StateFlow<MyRidesState> = _state.asStateFlow()

    private val _sideEffect = Channel<MyRidesSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        onEvent(MyRidesEvent.LoadRides)
    }

    fun onEvent(event: MyRidesEvent) {
        when (event) {
            is MyRidesEvent.LoadRides -> loadRides()
            is MyRidesEvent.Refresh -> loadRides(isRefresh = true)
            is MyRidesEvent.TabSelected -> {
                _state.update { it.copy(selectedTab = event.tab) }
            }
            is MyRidesEvent.RideClicked -> {
                screenModelScope.launch {
                    _sideEffect.send(MyRidesSideEffect.NavigateToBooking(event.rideId))
                }
            }
        }
    }

    private fun loadRides(isRefresh: Boolean = false) {
        screenModelScope.launch {
            _state.update {
                if (isRefresh) it.copy(isRefreshing = true)
                else it.copy(isLoading = true)
            }

            getMyRidesUseCase()
                .onSuccess { rides ->
                    _state.update {
                        it.copy(rides = rides, isLoading = false, isRefreshing = false, error = null)
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isLoading = false, isRefreshing = false, error = error.message)
                    }
                }
        }
    }
}

// 5. SCREEN (Composable)
@Composable
fun MyRidesScreen(
    viewModel: MyRidesViewModel = koinScreenModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is MyRidesSideEffect.NavigateToBooking -> { /* navigate */ }
                is MyRidesSideEffect.ShowError -> { /* show snackbar */ }
            }
        }
    }

    MyRidesContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}
```

---

## Navigation Architecture

### Screen Definitions

```kotlin
sealed class AppScreen : Screen {
    // Auth flow
    data object Login : AppScreen()
    data object Signup : AppScreen()
    data object ForgotPassword : AppScreen()
    data class VerifyOtp(val target: String) : AppScreen()

    // Main tabs
    data object Home : AppScreen()
    data object MyRides : AppScreen()
    data object Notifications : AppScreen()
    data object Profile : AppScreen()

    // Detail screens
    data class BookingDetail(val bookingId: String) : AppScreen()
    data class Chat(val conversationId: String) : AppScreen()
    data object Inbox : AppScreen()
    data object History : AppScreen()
    data object PostRideDetails : AppScreen()
    data object PostRideRouting : AppScreen()
    data object AddVehicle : AppScreen()
    data object LicenceVerification : AppScreen()
}
```

### Navigation Graph

```
AppNavigator (root)
│
├── isLoggedIn == false
│   └── AuthNavigator
│       ├── Login ←→ Signup
│       └── Login → ForgotPassword → VerifyOtp
│
└── isLoggedIn == true
    └── MainNavigator (TabNavigator)
        │
        ├── Tab 0: Search
        │   └── HomeScreen
        │       └── push → BookingDetail
        │
        ├── Tab 1: Bookings
        │   └── MyRidesScreen
        │       ├── push → BookingRequestsScreen
        │       └── push → HistoryScreen
        │
        ├── Tab 2: Alerts
        │   └── NotificationsScreen
        │
        └── Tab 3: Profile
            └── DriverProfileScreen
                ├── push → LicenceVerificationScreen
                └── push → AddVehicleScreen

        + Global overlays (any tab):
            ├── push → InboxScreen → ChatScreen
            └── push → PostRideDetails → PostRideRouting
```

### Bottom Navigation

```kotlin
enum class BottomNavTab(
    val title: String,
    val icon: ImageVector,
    val screen: AppScreen
) {
    SEARCH("Search", Icons.Outlined.Search, AppScreen.Home),
    BOOKINGS("Bookings", Icons.Outlined.DirectionsCar, AppScreen.MyRides),
    ALERTS("Alerts", Icons.Outlined.Notifications, AppScreen.Notifications),
    PROFILE("Profile", Icons.Outlined.Person, AppScreen.Profile)
}
```

---

## Network Layer

### ApiClient.kt — Ktor HttpClient

```kotlin
fun createHttpClient(tokenManager: TokenManager): HttpClient {
    return HttpClient {
        // JSON
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }

        // Auth — auto-attach JWT + auto-refresh on 401
        install(Auth) {
            bearer {
                loadTokens {
                    val access = tokenManager.getAccessToken()
                    val refresh = tokenManager.getRefreshToken()
                    if (access != null && refresh != null) {
                        BearerTokens(access, refresh)
                    } else null
                }
                refreshTokens {
                    val response = client.post(ApiRoutes.REFRESH_TOKEN) {
                        setBody(RefreshRequest(oldTokens?.refreshToken ?: ""))
                    }
                    val tokens = response.body<AuthResponse>()
                    tokenManager.saveTokens(tokens.accessToken, tokens.refreshToken)
                    BearerTokens(tokens.accessToken, tokens.refreshToken)
                }
            }
        }

        // Logging (debug only)
        install(Logging) {
            logger = Logger.NAPIER
            level = LogLevel.HEADERS
        }

        // Timeouts
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 30_000
        }

        // Default headers
        defaultRequest {
            url(ApiRoutes.BASE_URL)
            contentType(ContentType.Application.Json)
        }
    }
}
```

### ApiRoutes.kt

```kotlin
object ApiRoutes {
    const val BASE_URL = "https://api.juko.app/api/v1"

    // Auth
    const val LOGIN = "/auth/login"
    const val SIGNUP = "/auth/signup"
    const val REFRESH_TOKEN = "/auth/refresh"
    const val FORGOT_PASSWORD = "/auth/forgot-password"
    const val VERIFY_OTP = "/auth/verify-otp"
    const val RESET_PASSWORD = "/auth/reset-password"

    // User
    const val USER_PROFILE = "/users/me"
    const val UPLOAD_PROFILE_IMAGE = "/users/me/profile-image"
    const val USER_DOCUMENTS = "/users/me/documents"

    // Vehicles
    const val VEHICLES = "/vehicles"
    fun vehicle(id: String) = "/vehicles/$id"

    // Rides
    const val RIDES = "/rides"
    const val RIDES_SEARCH = "/rides/search"
    const val MY_RIDES = "/rides/mine"
    const val RIDE_HISTORY = "/rides/history"
    fun ride(id: String) = "/rides/$id"
    fun cancelRide(id: String) = "/rides/$id/cancel"

    // Bookings
    const val BOOKINGS = "/bookings"
    fun booking(id: String) = "/bookings/$id"
    fun acceptBooking(id: String) = "/bookings/$id/accept"
    fun declineBooking(id: String) = "/bookings/$id/decline"

    // Conversations
    const val CONVERSATIONS = "/conversations"
    fun conversation(id: String) = "/conversations/$id"
    fun sendMessage(id: String) = "/conversations/$id/messages"
    fun markRead(id: String) = "/conversations/$id/read"

    // Notifications
    const val NOTIFICATIONS = "/notifications"
    const val REGISTER_DEVICE = "/notifications/register-device"
    const val READ_ALL_NOTIFICATIONS = "/notifications/read-all"

    // WebSocket
    const val WS_CHAT = "wss://api.juko.app/ws/chat"
    const val WS_NOTIFICATIONS = "wss://api.juko.app/ws/notifications"
}
```

### NetworkResult Wrapper

```kotlin
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int, val message: String) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

// Extension for safe API calls
suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: ClientRequestException) {     // 4xx
        val error = e.response.body<ErrorResponse>()
        Result.failure(ApiException(e.response.status.value, error.message))
    } catch (e: ServerResponseException) {    // 5xx
        Result.failure(ApiException(500, "Server error. Please try again."))
    } catch (e: Exception) {
        Result.failure(ApiException(0, "No internet connection."))
    }
}
```

---

## WebSocket Layer

### WebSocketManager.kt

```kotlin
class WebSocketManager(
    private val client: HttpClient,
    private val tokenManager: TokenManager
) {
    private var session: DefaultClientWebSocketSession? = null
    private val _messages = MutableSharedFlow<WebSocketEvent>()
    val messages: SharedFlow<WebSocketEvent> = _messages.asSharedFlow()

    private var reconnectJob: Job? = null
    private var isConnected = false

    suspend fun connect() {
        val token = tokenManager.getAccessToken() ?: return

        try {
            client.webSocket("${ApiRoutes.WS_CHAT}?token=$token") {
                session = this
                isConnected = true

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val event = Json.decodeFromString<WebSocketEvent>(frame.readText())
                            _messages.emit(event)
                        }
                        else -> {}
                    }
                }
            }
        } catch (e: Exception) {
            isConnected = false
            scheduleReconnect()
        }
    }

    suspend fun sendMessage(message: WsOutgoingMessage) {
        session?.send(Frame.Text(Json.encodeToString(message)))
    }

    private fun scheduleReconnect() {
        reconnectJob?.cancel()
        reconnectJob = CoroutineScope(Dispatchers.Default).launch {
            delay(5000) // 5s backoff
            connect()
        }
    }

    fun disconnect() {
        reconnectJob?.cancel()
        session = null
        isConnected = false
    }
}
```

---

## Local Storage & Offline

### SQLDelight — Local Cache

```sql
-- shared/src/commonMain/sqldelight/com/juko/db/JukoDB.sq

-- Cached rides for offline viewing
CREATE TABLE rideCache (
    id TEXT NOT NULL PRIMARY KEY,
    driver_name TEXT NOT NULL,
    origin_city TEXT NOT NULL,
    destination_city TEXT NOT NULL,
    departure_time INTEGER NOT NULL,
    price_per_seat INTEGER NOT NULL,
    available_seats INTEGER NOT NULL,
    status TEXT NOT NULL,
    json_data TEXT NOT NULL,        -- Full serialized Ride JSON
    cached_at INTEGER NOT NULL
);

-- Cached messages for offline viewing
CREATE TABLE messageCache (
    id TEXT NOT NULL PRIMARY KEY,
    conversation_id TEXT NOT NULL,
    sender_id TEXT NOT NULL,
    content TEXT NOT NULL,
    message_type TEXT NOT NULL DEFAULT 'text',
    created_at INTEGER NOT NULL,
    is_synced INTEGER NOT NULL DEFAULT 1
);

-- Cached notifications
CREATE TABLE notificationCache (
    id TEXT NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    body TEXT NOT NULL,
    type TEXT NOT NULL,
    is_read INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL
);

-- Queries
selectAllRides:
SELECT * FROM rideCache ORDER BY departure_time DESC;

selectMessagesByConversation:
SELECT * FROM messageCache WHERE conversation_id = ? ORDER BY created_at ASC;

selectUnreadNotificationCount:
SELECT COUNT(*) FROM notificationCache WHERE is_read = 0;

insertRide:
INSERT OR REPLACE INTO rideCache VALUES ?;

insertMessage:
INSERT OR REPLACE INTO messageCache VALUES ?;
```

### Offline Strategy

```
┌─────────────┐     API Available?     ┌──────────────┐
│  ViewModel  │ ──── YES ────────────→ │  Remote API  │
│             │                        │  (Ktor)      │
│             │ ──── NO ─────────────→ │  Local DB    │
│             │                        │ (SQLDelight) │
└─────────────┘                        └──────────────┘

Strategy:
1. Always show cached data FIRST (instant UI)
2. Fetch fresh data from API in background
3. Update cache + UI when API responds
4. If API fails, keep showing cached data with "offline" indicator
```

---

## Dependency Injection

### Koin Modules

```kotlin
// AppModule.kt
val appModule = module {
    // Core
    single { createHttpClient(get()) }
    single { TokenManager(get()) }
    single { PreferencesManager(get()) }
    single { SessionManager(get(), get()) }
    single { WebSocketManager(get(), get()) }
}

val authModule = module {
    single { AuthApiService(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    factory { LoginUseCase(get()) }
    factory { SignupUseCase(get()) }
    factory { SendOtpUseCase(get()) }
    factory { VerifyOtpUseCase(get()) }
    viewModelOf(::LoginViewModel)
    viewModelOf(::SignupViewModel)
    viewModelOf(::ForgotPasswordViewModel)
}

val ridesModule = module {
    single { RidesApiService(get()) }
    single<RidesRepository> { RidesRepositoryImpl(get(), get()) }
    factory { GetMyRidesUseCase(get()) }
    factory { GetBookingRequestsUseCase(get()) }
    factory { AcceptBookingUseCase(get()) }
    factory { DeclineBookingUseCase(get()) }
    factory { GetRideHistoryUseCase(get()) }
    viewModelOf(::MyRidesViewModel)
    viewModelOf(::BookingRequestsViewModel)
    viewModelOf(::HistoryViewModel)
}

// ... similar modules for post_ride, chat, notifications, profile
```

---

## Platform-Specific Code

### expect/actual Pattern

```kotlin
// commonMain — Interface
expect class PlatformContext

expect fun getPlatformName(): String

expect class MapViewFactory {
    @Composable
    fun MapView(
        origin: LatLng,
        destination: LatLng,
        stops: List<LatLng>,
        modifier: Modifier
    )
}

expect class SecureStorage {
    fun saveToken(key: String, value: String)
    fun getToken(key: String): String?
    fun clearAll()
}

expect class ImagePickerLauncher {
    fun launch(onResult: (ByteArray?) -> Unit)
}
```

```kotlin
// androidMain — Implementation
actual class SecureStorage(context: Context) {
    private val keyStore = EncryptedSharedPreferences.create(
        "juko_secure_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    actual fun saveToken(key: String, value: String) {
        keyStore.edit().putString(key, value).apply()
    }
    actual fun getToken(key: String): String? = keyStore.getString(key, null)
    actual fun clearAll() = keyStore.edit().clear().apply()
}
```

```kotlin
// iosMain — Implementation
actual class SecureStorage {
    actual fun saveToken(key: String, value: String) {
        // iOS Keychain via Security framework
        val query = mapOf(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to key,
            kSecValueData to value.encodeToByteArray().toNSData()
        )
        SecItemAdd(query.toNSDictionary(), null)
    }
    // ...
}
```

### Platform Matrix

| Feature | Android (androidMain) | iOS (iosMain) |
|---|---|---|
| **Maps** | Google Maps Compose SDK | Apple MapKit (UIViewRepresentable) |
| **Secure Storage** | EncryptedSharedPreferences | iOS Keychain |
| **Image Picker** | ActivityResultContracts | PHPickerViewController |
| **Push Token** | Firebase Messaging (FCM) | APNs + Firebase bridge |
| **Location** | FusedLocationProvider | CLLocationManager |
| **HTTP Engine** | OkHttp | Darwin (NSURLSession) |
| **SQLite Driver** | AndroidSqliteDriver | NativeSqliteDriver |
| **Biometrics** | BiometricPrompt | LAContext (Face ID / Touch ID) |

---

## Image Loading & Media

### Coil 3 Setup

```kotlin
// In App.kt or DI module
val imageLoader = ImageLoader.Builder(context)
    .components {
        add(KtorNetworkFetcherFactory(httpClient))
    }
    .memoryCachePolicy(CachePolicy.ENABLED)
    .diskCachePolicy(CachePolicy.ENABLED)
    .diskCache {
        DiskCache.Builder()
            .directory(cacheDir / "image_cache")
            .maxSizeBytes(50L * 1024 * 1024) // 50 MB
            .build()
    }
    .build()

// Usage in composables
@Composable
fun DriverAvatar(imageUrl: String?, size: Dp = 48.dp) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Driver photo",
        modifier = Modifier
            .size(size)
            .clip(CircleShape),
        placeholder = painterResource(Res.drawable.avatar_placeholder),
        error = painterResource(Res.drawable.avatar_placeholder),
        contentScale = ContentScale.Crop
    )
}
```

### File Upload (Vehicle Photos, DL)

```kotlin
suspend fun uploadImage(
    bytes: ByteArray,
    endpoint: String,
    filename: String
): Result<String> = safeApiCall {
    val response = httpClient.submitFormWithBinaryData(
        url = endpoint,
        formData = formData {
            append("file", bytes, Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=$filename")
                append(HttpHeaders.ContentType, "image/jpeg")
            })
        }
    )
    response.body<UploadResponse>().url
}
```

---

## Security

| Concern | Implementation |
|---|---|
| **Token Storage** | Android EncryptedSharedPreferences / iOS Keychain |
| **JWT Auto-Refresh** | Ktor Auth plugin — transparent 401 → refresh → retry |
| **Certificate Pinning** | OkHttp CertificatePinner (Android) / URLSessionDelegate (iOS) |
| **Input Validation** | Client-side validators before API call (email, phone, OTP format) |
| **Sensitive Screens** | Screenshot prevention on Login/OTP screens (FLAG_SECURE on Android) |
| **ProGuard/R8** | Obfuscation enabled for release builds |
| **Biometric Lock** | Optional biometric auth on app resume (LAContext / BiometricPrompt) |
| **No Secrets in Code** | API base URL via BuildConfig / Xcode schemes, never hardcoded |

---

## Performance Optimization

| Area | Strategy |
|---|---|
| **List Rendering** | `LazyColumn` with `key` parameter for stable recomposition |
| **Image Loading** | Coil disk + memory cache (50MB), placeholder shimmer |
| **API Calls** | Debounce search (300ms), deduplicate in-flight requests |
| **State Updates** | `distinctUntilChanged()` on StateFlows, avoid unnecessary recomposition |
| **Pagination** | Cursor-based infinite scroll via `Pager` pattern in ViewModel |
| **Startup** | Lazy Koin module loading, splash screen while initializing |
| **Bundle Size** | R8 minification, resource shrinking, per-ABI APK splits |
| **Compose** | `remember`, `derivedStateOf`, `Immutable` annotations on models |
| **Offline First** | Show cached data instantly → refresh in background |
| **WebSocket** | Single persistent connection, auto-reconnect with exponential backoff |

---

## Testing Strategy

| Layer | Tool | What to Test |
|---|---|---|
| **Unit Tests** | kotlin.test + Turbine | ViewModels, UseCases, Mappers, Validators |
| **Repository Tests** | kotlin.test + MockEngine | API response parsing, error handling |
| **UI Tests (Android)** | Compose UI Test | Screen rendering, interactions, navigation |
| **UI Tests (iOS)** | XCUITest | Critical flows (login, post ride) |
| **Snapshot Tests** | Paparazzi (Android) | Visual regression for components |
| **Integration** | Ktor MockEngine | Full feature flow with mocked API |

### Test Structure

```
shared/src/commonTest/kotlin/com/juko/
├── feature/
│   ├── auth/
│   │   ├── LoginViewModelTest.kt
│   │   ├── SignupViewModelTest.kt
│   │   └── AuthRepositoryTest.kt
│   ├── rides/
│   │   ├── MyRidesViewModelTest.kt
│   │   └── RidesRepositoryTest.kt
│   └── ...
├── core/
│   ├── ValidatorsTest.kt
│   ├── DateTimeFormatterTest.kt
│   └── CurrencyFormatterTest.kt
└── util/
    └── FakeData.kt                    # Test fixtures
```

### Example ViewModel Test

```kotlin
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private val fakeRepo = FakeAuthRepository()

    @BeforeTest
    fun setup() {
        viewModel = LoginViewModel(LoginUseCase(fakeRepo))
    }

    @Test
    fun `login success updates state`() = runTest {
        viewModel.state.test {
            // Initial state
            assertEquals(LoginState(), awaitItem())

            // Enter credentials
            viewModel.onEvent(LoginEvent.EmailChanged("driver@juko.app"))
            viewModel.onEvent(LoginEvent.PasswordChanged("password123"))

            // Submit
            viewModel.onEvent(LoginEvent.Submit)

            // Loading
            val loading = awaitItem()
            assertTrue(loading.isLoading)

            // Success
            val success = awaitItem()
            assertFalse(success.isLoading)
            assertTrue(success.isSuccess)
        }
    }
}
```

---

## Build & Release

### Android

```kotlin
// androidApp/build.gradle.kts
android {
    namespace = "com.juko.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.juko.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://staging-api.juko.app/api/v1\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "BASE_URL", "\"https://api.juko.app/api/v1\"")
        }
    }
}
```

### iOS

```
iosApp/
├── Debug.xcconfig        → JUKO_BASE_URL = https://staging-api.juko.app/api/v1
├── Release.xcconfig      → JUKO_BASE_URL = https://api.juko.app/api/v1
└── Info.plist
```

### CI/CD

```
Push to main
    │
    ├── ✅ Lint (ktlint, detekt)
    ├── ✅ Unit Tests (commonTest)
    ├── ✅ Android Build (debug APK)
    ├── ✅ iOS Build (debug .app)
    │
    └── Tag release (v1.0.0)
        ├── 🤖 Android → Signed AAB → Play Console (Internal Track)
        └── 🍎 iOS → Archive → TestFlight
```

---

## Dependency Versions

```toml
# gradle/libs.versions.toml

[versions]
kotlin = "2.1.0"
agp = "8.7.0"
compose-multiplatform = "1.7.0"
compose-compiler = "1.5.14"

ktor = "3.0.0"
koin = "4.0.0"
voyager = "1.1.0"
sqldelight = "2.0.2"
coil = "3.0.0"

kotlinx-coroutines = "1.9.0"
kotlinx-serialization = "1.7.0"
kotlinx-datetime = "0.6.0"

napier = "2.7.1"
multiplatform-settings = "1.2.0"
moko-permissions = "0.18.0"
google-maps-compose = "6.2.0"

[libraries]
# Compose
compose-ui = { module = "org.jetbrains.compose.ui:ui", version.ref = "compose-multiplatform" }
compose-material3 = { module = "org.jetbrains.compose.material3:material3", version.ref = "compose-multiplatform" }

# Ktor
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }
ktor-client-contentNegotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
ktor-client-websockets = { module = "io.ktor:ktor-client-websockets", version.ref = "ktor" }
ktor-client-auth = { module = "io.ktor:ktor-client-auth", version.ref = "ktor" }
ktor-client-logging = { module = "io.ktor:ktor-client-logging", version.ref = "ktor" }
ktor-serialization-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }

# Voyager
voyager-navigator = { module = "cafe.adriel.voyager:voyager-navigator", version.ref = "voyager" }
voyager-tabNavigator = { module = "cafe.adriel.voyager:voyager-tab-navigator", version.ref = "voyager" }
voyager-screenModel = { module = "cafe.adriel.voyager:voyager-screenmodel", version.ref = "voyager" }
voyager-transitions = { module = "cafe.adriel.voyager:voyager-transitions", version.ref = "voyager" }

# Koin
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-compose = { module = "io.insert-koin:koin-compose", version.ref = "koin" }
koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }

# SQLDelight
sqldelight-coroutines = { module = "app.cash.sqldelight:coroutines-extensions", version.ref = "sqldelight" }
sqldelight-android = { module = "app.cash.sqldelight:android-driver", version.ref = "sqldelight" }
sqldelight-native = { module = "app.cash.sqldelight:native-driver", version.ref = "sqldelight" }

# Coil
coil-compose = { module = "io.coil-kt.coil3:coil-compose", version.ref = "coil" }
coil-network-ktor = { module = "io.coil-kt.coil3:coil-network-ktor3", version.ref = "coil" }

# Utility
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinx-coroutines" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization" }
kotlinx-datetime = { module = "org.jetbrains.kotlinx:kotlinx-datetime", version.ref = "kotlinx-datetime" }
napier = { module = "io.github.aakira:napier", version.ref = "napier" }
multiplatformSettings = { module = "com.russhwolf:multiplatform-settings-no-arg", version.ref = "multiplatform-settings" }

# Maps (Android only)
google-maps-compose = { module = "com.google.maps.android:maps-compose", version.ref = "google-maps-compose" }

[plugins]
kotlinMultiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
composeMultiplatform = { id = "org.jetbrains.compose", version.ref = "compose-multiplatform" }
composeCompiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlinxSerialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
sqldelight = { id = "app.cash.sqldelight", version.ref = "sqldelight" }
androidApplication = { id = "com.android.application", version.ref = "agp" }
androidLibrary = { id = "com.android.library", version.ref = "agp" }
```

---

## Code Sharing Summary

```
┌──────────────────────────────────────────────┐
│              Code Sharing Target             │
│                                              │
│   commonMain (shared)          ~95%          │
│   ████████████████████████████████████████   │
│                                              │
│   androidMain (platform)       ~3%           │
│   ██                                         │
│                                              │
│   iosMain (platform)           ~2%           │
│   █                                          │
│                                              │
│   androidApp (entry)           < 1%          │
│   ▏                                          │
│                                              │
│   iosApp (entry)               < 1%          │
│   ▏                                          │
└──────────────────────────────────────────────┘
```

---

*Frontend spec for Stitch project `projects/4074151794218231363` — August 24, 2026*

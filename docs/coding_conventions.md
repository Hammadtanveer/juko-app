# 🚗 JUKO — Coding Conventions & Architecture Rules

> **Target:** Kotlin Multiplatform + Compose Multiplatform  
> **Purpose:** Strict rules for AI code generation. **Do not deviate from these rules under any circumstances.**

---

## 1. Architecture: MVVM + Clean Architecture

All features must be split into three layers: `presentation`, `domain`, and `data`.

### Rule 1.1: Dependency Rule
- `presentation` depends on `domain`.
- `data` depends on `domain` (implements interfaces).
- `domain` depends on NOTHING. It contains pure Kotlin data classes and interfaces. **Never put Ktor, Compose, or SQLDelight imports in the domain layer.**

### Rule 1.2: Feature Folder Structure
When creating a new feature, ALWAYS use this exact structure:
```text
feature/[feature_name]/
  ├── data/
  │   ├── dto/                  # Network/DB models
  │   ├── mapper/               # Dto to Domain mappers
  │   ├── remote/               # ApiService (Ktor calls)
  │   └── repository/           # RepositoryImpl
  ├── domain/
  │   ├── model/                # Pure data classes
  │   ├── repository/           # Repository interfaces
  │   └── usecase/              # Business logic classes
  └── presentation/
      └── [screen_name]/
          ├── [Name]Screen.kt   # UI Composable
          ├── [Name]State.kt    # State & Events
          └── [Name]ViewModel.kt# Logic
```

---

## 2. State Management (UDF)

Every screen MUST use Unidirectional Data Flow.

### Rule 2.1: The State Contract
Create a single file `[Name]State.kt` containing three things:
1. `State`: A data class representing the UI.
2. `Event`: A sealed interface for user actions.
3. `SideEffect`: A sealed interface for one-off actions (Navigation, Toasts).

### Rule 2.2: ViewModel Structure
ViewModels must implement Voyager's `ScreenModel` and follow this exact template:
```kotlin
class LoginViewModel(
    private val loginUseCase: LoginUseCase // Inject UseCases, NEVER Repositories directly
) : ScreenModel {

    // 1. State
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    // 2. Side Effects
    private val _effect = Channel<LoginSideEffect>()
    val effect = _effect.receiveAsFlow()

    // 3. Single Entry Point
    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> _state.update { it.copy(email = event.value) }
            is LoginEvent.Submit -> performLogin()
        }
    }

    private fun performLogin() {
        screenModelScope.launch {
            // Logic goes here
        }
    }
}
```

---

## 3. Compose UI Rules

### Rule 3.1: Screen Structure
Every `[Name]Screen.kt` must contain exactly two Composables:
1. **The Route Composable:** Handles DI and collects state.
2. **The Content Composable:** Pure UI, takes State and Lambdas. This makes previews and testing possible.

```kotlin
// 1. The Route (Voyager Screen)
class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<LoginViewModel>()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                // Handle navigation here
            }
        }

        LoginContent(state = state, onEvent = viewModel::onEvent)
    }
}

// 2. The Pure UI
@Composable
private fun LoginContent(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit
) {
    // Build UI using Juko* components here. NO BUSINESS LOGIC.
}
```

### Rule 3.2: Modifiers
- **Always** pass a `modifier: Modifier = Modifier` as the first optional parameter in every custom Composable.
- **Never** hardcode sizes or padding unless absolutely necessary. Use `LocalSpacing.current`.

---

## 4. Networking & Error Handling

### Rule 4.1: API Calls
- **Never** make API calls directly in ViewModels.
- **Always** wrap API calls in the `safeApiCall` block inside the Repository implementation to catch HTTP errors and return a `Result<T>`.

```kotlin
// DON'T do this:
val response = client.get("/users").body<User>()

// DO this (in RepositoryImpl):
override suspend fun getUser(): Result<User> {
    return safeApiCall {
        apiService.getUser().toDomain()
    }
}
```

---

## 5. Naming Conventions

| Concept | Rule | Example |
|---|---|---|
| **ViewModel** | Suffix with `ViewModel` | `LoginViewModel` |
| **State** | Suffix with `State` | `LoginState` |
| **Event** | Suffix with `Event` | `LoginEvent` |
| **Side Effect** | Suffix with `SideEffect` | `LoginSideEffect` |
| **UseCase** | Verb + Noun + `UseCase` | `GetMyRidesUseCase` |
| **API Models** | Suffix with `Dto` | `RideDto` |
| **Domain Models** | Plain Noun | `Ride` |
| **Repository** | Noun + `Repository` (Interface) | `AuthRepository` |
| **Repo Impl** | Noun + `RepositoryImpl` | `AuthRepositoryImpl` |

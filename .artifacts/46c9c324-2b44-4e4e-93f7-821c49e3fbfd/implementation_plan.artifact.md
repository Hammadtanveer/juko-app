# Phase 2.2 — Auth Presentation Layer Implementation Plan

This plan implements the Login screen's presentation layer following the JUKO architecture rules (MVVM + Clean Architecture + UDF).

## User Review Required

> [!IMPORTANT]
> - I will be creating `Validators.kt` in `core/util` as it is a dependency for the ViewModel's validation logic, even though not explicitly asked for.
> - The navigation logic will be triggered via `LoginSideEffect`, which should be handled in the `LoginScreen`'s `LaunchedEffect`.

## Proposed Changes

### Core Utilities

#### [NEW] [Validators.kt](file:///D:/Projects/Juko/shared/src/commonMain/kotlin/com/juko/app/core/util/Validators.kt)
- Implement `validateEmail` and `validatePassword` as pure functions using regex from `validation_rules.md`.

---

### Auth Presentation Layer (Login)

#### [NEW] [LoginState.kt](file:///D:/Projects/Juko/shared/src/commonMain/kotlin/com/juko/app/feature/auth/presentation/login/LoginState.kt)
- Define `LoginState` (email, password, isLoading, etc.).
- Define `LoginEvent` (EmailChanged, PasswordChanged, Submit, etc.).
- Define `LoginSideEffect` (NavigateToHome, NavigateToSignup, etc.).

#### [NEW] [LoginViewModel.kt](file:///D:/Projects/Juko/shared/src/commonMain/kotlin/com/juko/app/feature/auth/presentation/login/LoginViewModel.kt)
- Voyager `ScreenModel` implementation.
- Handles user input, validation, and triggers `LoginUseCase`.
- Exposes `StateFlow<LoginState>` and `Flow<LoginSideEffect>`.

#### [NEW] [LoginScreen.kt](file:///D:/Projects/Juko/shared/src/commonMain/kotlin/com/juko/app/feature/auth/presentation/login/LoginScreen.kt)
- Voyager `Screen` implementation.
- `LoginContent` stateless composable using `Juko` components library.
- Collects state and handles side effects.

---

### Dependency Injection

#### [MODIFY] [AuthModule.kt](file:///D:/Projects/Juko/shared/src/commonMain/kotlin/com/juko/app/feature/auth/di/AuthModule.kt)
- Add `factory { LoginViewModel(get()) }` to the module.

## Verification Plan

### Automated Tests
- Create `LoginViewModelTest.kt` to verify state updates on input and use case interaction.

### Manual Verification
- Render `LoginScreen` in the app and verify:
    - Input fields update state.
    - Validation errors show when fields are empty or invalid.
    - Loading state shows during "Login" click.
    - Success navigates to Home (simulated via SideEffect).

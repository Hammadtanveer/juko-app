# 🚗 JUKO — Component Library (Compose Multiplatform)

> **Design System:** Velocity Drive  
> **Rule of Thumb:** NEVER use raw Compose components (like `Button`, `TextField`, or `Card`) directly in feature screens. NEVER hardcode colors or dp values. **Always use the `Juko*` prefixed components defined below.**

---

## 1. Buttons

### `JukoButton` (Primary Action)
Used for the main action on a screen (e.g., "Login", "Post a Ride", "Accept").
```kotlin
@Composable
fun JukoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    color: JukoButtonColor = JukoButtonColor.Primary // Primary (Blue) or Success (Green)
)
```

### `JukoGhostButton` (Secondary Action)
Used for secondary actions (e.g., "Forgot Password?", "Cancel").
```kotlin
@Composable
fun JukoGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false // If true, text is Red (e.g., "Decline", "Logout")
)
```

---

## 2. Text Inputs

### `JukoTextField` (Standard Input)
Used for standard text entry (Email, Name, Vehicle Make). Features a bold, all-caps label above the input.
```kotlin
@Composable
fun JukoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String, // e.g., "EMAIL"
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
)
```

### `JukoOtpInput` (OTP Verification)
Used strictly on the OTP verification screen.
```kotlin
@Composable
fun JukoOtpInput(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    isError: Boolean = false
)
```

---

## 3. Data Display & Cards

### `JukoCard` (Base Surface)
The foundational surface for ride details, booking requests, etc. Has standard 12dp border radius and slight shadow.
```kotlin
@Composable
fun JukoCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
)
```

### `JukoAvatar` (Profile Pictures)
Always use this for displaying driver/passenger faces. Handles the fallback placeholder automatically.
```kotlin
@Composable
fun JukoAvatar(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
)
```

### `JukoStatusChip` (Ride/Booking Status)
Small pill-shaped indicator. Colors auto-adjust based on the status enum.
```kotlin
@Composable
fun JukoStatusChip(
    status: RideStatus, // Defines color: SUCCESS (Completed), WARNING (Pending), NEUTRAL (Scheduled)
    modifier: Modifier = Modifier
)
```

---

## 4. Navigation & Structure

### `JukoTopBar` (Header)
Used at the top of all inner screens (not on the main bottom-nav tabs).
```kotlin
@Composable
fun JukoTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
)
```

### `JukoSegmentedControl` (In-Screen Tabs)
Used for switching views within a screen (e.g., Upcoming / Active / Completed).
```kotlin
@Composable
fun JukoSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
)
```

---

## 5. State & Feedback

### `JukoEmptyState`
Used when lists are empty (no rides, no messages, no notifications).
```kotlin
@Composable
fun JukoEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionButton: @Composable (() -> Unit)? = null
)
```

### `JukoLoadingScreen`
Full-screen centered loading indicator (uses the brand Blue).
```kotlin
@Composable
fun JukoLoadingScreen(modifier: Modifier = Modifier)
```

---

## 6. Layout & Spacing Rules

When building screens, **always** use the `LocalSpacing` composition local instead of hardcoding `dp` values.

```kotlin
// WRONG
Column(modifier = Modifier.padding(16.dp)) { ... }

// CORRECT
val spacing = LocalSpacing.current
Column(modifier = Modifier.padding(spacing.md)) { ... }
```

**Spacing Scale:**
- `spacing.xs` = 8.dp (tight gaps, inside cards)
- `spacing.sm` = 12.dp (between related text elements)
- `spacing.md` = 16.dp (standard screen padding, gaps between form fields)
- `spacing.lg` = 24.dp (sections separation)
- `spacing.xl` = 32.dp (major layout separation)

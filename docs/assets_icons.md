# 🚗 JUKO — Assets & Icons Dictionary

> **Purpose:** This document strictly defines which icons and assets must be used across the application. AI agents must use the exact Compose `Icons` syntax provided below to prevent compilation errors and ensure visual consistency.

---

## 1. Icon Style Rules

1. **Default Style:** Always use `Icons.Outlined` for standard UI elements (buttons, lists, fields).
2. **Active Style:** Only use `Icons.Filled` for the currently selected tab in the Bottom Navigation, or for solid state indicators (like a filled star for ratings).
3. **Mirrored Icons:** Use `Icons.AutoMirrored.Outlined.*` for directional icons (like Back arrows) to support RTL languages automatically.

---

## 2. Compose Material Icons Mapping

### Bottom Navigation Tabs
| Tab | Inactive (Outlined) | Active (Filled) |
|---|---|---|
| **Search** | `Icons.Outlined.Search` | `Icons.Filled.Search` |
| **Bookings** | `Icons.Outlined.DirectionsCar` | `Icons.Filled.DirectionsCar` |
| **Alerts** | `Icons.Outlined.Notifications` | `Icons.Filled.Notifications` |
| **Profile** | `Icons.Outlined.Person` | `Icons.Filled.Person` |

### General Navigation & Actions
| Action | Compose Icon Code |
|---|---|
| **Back Button** | `Icons.AutoMirrored.Outlined.ArrowBack` |
| **Close / Dismiss** | `Icons.Outlined.Close` |
| **Add / Create** | `Icons.Outlined.Add` |
| **Edit** | `Icons.Outlined.Edit` |
| **Send Message** | `Icons.AutoMirrored.Outlined.Send` |
| **Settings** | `Icons.Outlined.Settings` |
| **Logout** | `Icons.AutoMirrored.Outlined.Logout` |

### Ride & Booking Attributes
| Attribute | Compose Icon Code |
|---|---|
| **Origin / Pickup** | `Icons.Outlined.RadioButtonChecked` |
| **Destination / Drop** | `Icons.Outlined.Place` (or `LocationOn`) |
| **Stops / Route** | `Icons.Outlined.MoreVert` |
| **Date & Time** | `Icons.Outlined.Schedule` (or `CalendarToday`) |
| **Seats Available** | `Icons.Outlined.Group` (or `Person`) |
| **Price / Earnings**| `Icons.Outlined.Payments` |
| **Rating / Star** | `Icons.Filled.Star` (Yellow/Brand color) |

### Vehicle & Preferences
| Attribute | Compose Icon Code |
|---|---|
| **Smoking Allowed** | `Icons.Outlined.SmokingRooms` |
| **Pets Allowed** | `Icons.Outlined.Pets` |
| **Luggage** | `Icons.Outlined.Luggage` |

---

## 3. Custom Image Assets (Drawables)

For images that are not Material icons, use KMP's Compose Resources `painterResource`. 
*Note: AI agents should assume these files exist in `composeApp/src/commonMain/composeResources/drawable/`.*

| Asset Description | Code Usage |
|---|---|
| **JUKO App Logo** | `painterResource(Res.drawable.logo_juko)` |
| **Driver Avatar Placeholder** | `painterResource(Res.drawable.ic_avatar_placeholder)` |
| **Empty Inbox Illustration** | `painterResource(Res.drawable.ill_empty_chat)` |
| **Empty Notifications** | `painterResource(Res.drawable.ill_empty_alerts)` |
| **Empty Rides** | `painterResource(Res.drawable.ill_empty_rides)` |
| **Car Placeholder** | `painterResource(Res.drawable.ic_car_placeholder)` |

---

## 🛑 Rule for AI Generation
**NEVER** invent or guess an icon name (e.g., do not write `Icons.Outlined.Car` as it does not exist in the standard Material library; use `DirectionsCar`). If a specific icon is not listed above, use the closest matching standard Compose Material 3 icon.

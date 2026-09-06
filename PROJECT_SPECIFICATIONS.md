# 🚗 JUKO — Master Project Specifications & Roadmap

---

## ⭐ Non-Negotiable Business Rules

- [x] **One Ride Structure**: One Source + One Final Destination + Zero or More Optional Stops.
- [x] **Stops are Pickup Points Only**: Stops are NOT destinations. The Final Destination is fixed and identical for all passengers.
- [x] **Forward-Only Travel**: Passengers can search & book `Source → Final Destination` or `Stop → Final Destination`. Backward travel or stop-to-stop rides are strictly prohibited.
- [x] **Driver Pricing**: Driver enters **ONE full-route price** (`Source → Final Destination`). System automatically calculates applicable `Stop → Final Destination` fare dynamically based on remaining distance/stops.
- [x] **Vehicle Specifications**: Fixed **5-Seater** or **7-Seater** selector only. **No AC option** (not in backend schema). Maximum **3 vehicle photos**.
- [x] **Ride Editing Lock**: 
  - `0 bookings` → Edit allowed (stops, price, date/time, vehicle).
  - `1+ bookings` → Edit locked and disabled.
- [x] **My Rides Role Separation**:
  - Customer / Passenger view: **Bookings + History**
  - Driver view: **Published + Requests + History**
- [x] **Two-Way Review System**:
  - Customer → Driver review (independent)
  - Driver → Customer review (independent)
  - Accessible via `My Rides → History → Completed Ride → Review`
- [x] **Main Sidebar Structure (via ☰ on Home)**:
  1. Ratings
  2. Help
  3. Password (Change Password)
  4. Terms and Conditions
  5. Data Protection
  6. Log Out
  7. Close My Account

---

## 📋 Detailed Module Checklist

### 1. Main Search Screen
- [x] Pickup / Source location input
- [x] Final Destination location input
- [x] Search rides using `Pickup → Final Destination`
- [x] Allow searching from driver's `Stop → Final Destination`
- [x] Display available rides with calculated per-seat fare
- [ ] API integration: Google Places / Location Search API & real search queries
- [ ] Handle no rides found state & invalid location errors

### 2. Location & Route API / Map Integration
- [ ] Real Location & Geocoding API integration
- [ ] Google Routes API integration (polyline, distance, duration)
- [ ] Route calculation for `Source → Stops → Final Destination`
- [ ] Validate whether pickup stop lies accurately on the driver's route

### 3. Stop Management (Driver & Passenger)
- [x] Dynamic add/remove stops during ride posting
- [x] Strict chronological stop order preservation
- [x] Forward-only index logic: selecting pickup at index $k$ disables $< k$, keeps $> k$ and Destination
- [x] Stops are pickup-only boarding points; drop-off is always Final Destination

### 4. Stop-Based Dynamic Pricing
- [x] Driver enters ONE full-route price
- [x] Dynamic fare calculation per stop based on remaining route/distance
- [x] Display dynamic price updates in search results and ride details
- [x] No manual price entry needed per stop from driver

### 5. Vehicle Management (Add / Edit)
- [x] Dual-mode Add & Edit Vehicle screen (`AddVehicleScreen.kt`)
- [x] Vehicle Brand & Model fields (Brand + Model combined)
- [x] Fixed 5-Seater / 7-Seater radio selector
- [x] AC option completely removed
- [x] Max 3 vehicle photos with delete icon (`✕`) and replacement support

### 6. Create & Publish Ride
- [x] Step 1: Route & Pricing (`PostRideRouteScreen.kt`)
- [x] Step 2: Details & Preferences (`PostRideDetailsScreen.kt`)
- [x] Full-route price input & dynamic segment calculation
- [x] Driver Profile Guard (`DriverProfileManager.kt`): 10-digit phone, front & back licence, registered vehicle validation before publishing

### 7. Ride Editing Restrictions
- [ ] Check booking count on published ride
- [ ] `0 bookings` → Show "Edit Ride" option
- [ ] `1+ bookings` → Hide/disable "Edit Ride" and show tooltip/dialog: *"Ride cannot be edited after passengers have booked"*

### 8. Seat Management
- [x] Available seats based on 5-Seater / 7-Seater capacity
- [x] Seat steppers & seat selection in `RideDetailsScreen.kt`
- [ ] Auto-decrement available seats on confirmed booking
- [ ] Prevent exceeding vehicle capacity or booking when full

### 9. Booking / Request System
- [x] Booking confirmation dialog with selected stop, time, and dynamic price
- [ ] Incoming Requests Screen for Driver with passenger boarding stop, requested seats, accept/reject actions
- [ ] Lock ride editing automatically upon accepting 1st booking

### 10. Profile & Verification
- [x] Common user profile details
- [x] 10-Digit verified phone number validation
- [x] Driver Licence (Front & Back) photo upload cards
- [x] Registered vehicle list with Edit/Add shortcuts
- [x] Profile completion check dialog preventing unauthorized publishing

### 11. My Rides Screen
- [ ] Passenger Mode: **Bookings** (upcoming rides, boarding pass) + **History**
- [ ] Driver Mode: **Published** (with "View Passengers" per stop) + **Requests** (accept/reject) + **History**
- [ ] Filter by Date Range + Status (Completed, Cancelled)

### 12. Two-Way Review System
- [ ] Entry point: `My Rides → History → Completed Ride`
- [ ] Customer rates & reviews Driver (1-5 stars + comment)
- [ ] Driver rates & reviews Customer (1-5 stars + comment)
- [ ] Prevent duplicate reviews; hide button once submitted
- [ ] Display average ratings & review badges on Profile and Sidebar

### 13. Main Sidebar Navigation (☰ Hamburger Menu)
- [ ] Hamburger menu button in `HomeScreen` top bar opening drawer/sidebar
- [ ] **Ratings**: View all received ratings and customer/driver feedback
- [ ] **Help**: Support options and FAQ
- [ ] **Password**: Change password screen (current, new, confirm password validation)
- [ ] **Terms and Conditions**: Legal terms viewer
- [ ] **Data Protection**: Privacy policy & data protection viewer
- [ ] **Log Out**: Confirmation modal $\to$ clear session $\to$ redirect to Auth
- [ ] **Close My Account**: Warning modal with confirmation $\to$ account deletion flow

### 14. Date & Time Validation
- [x] Date & Time pickers for departure and arrival
- [ ] Validation preventing selection of past dates / times
- [ ] Dynamic current date integration

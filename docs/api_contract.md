# 🚗 JUKO — API Contract (Draft / Dummy)

> **Version:** 1.0 (DRAFT)  
> **Date:** August 25, 2026  
> **Note:** These are **dummy contracts** created so the frontend team can build UI and logic using Mock Engines (like Ktor MockEngine or fake repositories) while waiting for the backend team. Update this file once the real backend Swagger/OpenAPI spec is available.

---

## Base Information

- **Base URL (Staging):** `https://staging-api.juko.app/api/v1`
- **Authentication:** `Authorization: Bearer <access_token>`
- **Content-Type:** `application/json`

---

## 1. Authentication (`/auth`)

### 1.1 Login
**`POST /auth/login`**

**Request:**
```json
{
  "email": "driver@juko.app",
  "password": "Password123!"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIsIn...",
    "refresh_token": "def456...",
    "expires_in": 1800,
    "user": {
      "id": "usr_12345",
      "full_name": "Rajesh Sharma",
      "email": "driver@juko.app",
      "is_verified": true
    }
  }
}
```

### 1.2 Sign Up
**`POST /auth/signup`**

**Request:**
```json
{
  "full_name": "Rajesh Sharma",
  "email": "driver@juko.app",
  "phone": "+919876543210",
  "password": "Password123!"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "OTP sent to registered phone number",
  "data": {
    "user_id": "usr_12345"
  }
}
```

### 1.3 Verify OTP
**`POST /auth/verify-otp`**

**Request:**
```json
{
  "user_id": "usr_12345",
  "otp": "123456"
}
```

**Response (200 OK):**
*(Same structure as Login response)*

---

## 2. Profile & Vehicles (`/users`, `/vehicles`)

### 2.1 Get Driver Profile
**`GET /users/me`**
*Requires Auth*

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": "usr_12345",
    "full_name": "Rajesh Sharma",
    "email": "driver@juko.app",
    "phone": "+919876543210",
    "profile_image_url": "https://s3.juko.app/avatars/usr_12345.jpg",
    "is_verified": true,
    "verification_status": "verified",
    "rating": 4.8,
    "total_rides": 42
  }
}
```

### 2.2 Add Vehicle
**`POST /vehicles`**
*Requires Auth*

**Request:**
```json
{
  "make": "Maruti Suzuki",
  "model": "Swift Dzire",
  "year": 2023,
  "color": "White",
  "plate_number": "DL 01 AB 1234",
  "total_seats": 4,
  "photo_urls": [
    "https://s3.juko.app/temp/upload_1.jpg"
  ]
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "id": "veh_999",
    "status": "active"
  }
}
```

---

## 3. Rides (`/rides`)

### 3.1 Post a Ride
**`POST /rides`**
*Requires Auth*

**Request:**
```json
{
  "vehicle_id": "veh_999",
  "origin_lat": 28.6139,
  "origin_lng": 77.2090,
  "origin_city": "New Delhi",
  "destination_lat": 26.9124,
  "destination_lng": 75.7873,
  "destination_city": "Jaipur",
  "departure_time": "2026-08-28T18:00:00Z",
  "available_seats": 3,
  "price_per_seat": 45000, 
  "smoking_allowed": false,
  "pets_allowed": false,
  "luggage_size": "medium"
}
```
*(Note: price_per_seat is in paisa. 45000 = ₹450)*

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "id": "ride_101",
    "status": "scheduled"
  }
}
```

### 3.2 Get My Rides
**`GET /rides/mine?status=scheduled`**
*Requires Auth*

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": "ride_101",
      "origin_city": "New Delhi",
      "destination_city": "Jaipur",
      "departure_time": "2026-08-28T18:00:00Z",
      "available_seats": 3,
      "booked_seats": 1,
      "price_per_seat": 45000,
      "status": "scheduled"
    }
  ],
  "meta": {
    "total": 1,
    "page": 1
  }
}
```

---

## 4. Bookings (`/bookings`)

### 4.1 Get Booking Requests
**`GET /bookings?ride_id=ride_101`**
*Requires Auth*

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": "book_555",
      "ride_id": "ride_101",
      "passenger": {
        "id": "usr_987",
        "name": "Sneha Gupta",
        "rating": 4.9,
        "avatar_url": "https://s3.juko.app/avatars/usr_987.jpg"
      },
      "seats_requested": 1,
      "total_price": 45000,
      "status": "pending",
      "pickup_city": "New Delhi",
      "drop_city": "Jaipur",
      "created_at": "2026-08-26T10:15:00Z"
    }
  ]
}
```

### 4.2 Accept Booking
**`PATCH /bookings/book_555/accept`**
*Requires Auth*

**Request:** `Empty Body`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Booking accepted",
  "data": {
    "conversation_id": "conv_777"
  }
}
```

---

## 5. Chat (`/conversations`)

### 5.1 Get Inbox
**`GET /conversations`**
*Requires Auth*

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": "conv_777",
      "passenger_name": "Sneha Gupta",
      "passenger_avatar": "https://s3.juko.app/avatars/usr_987.jpg",
      "last_message": "Great, I'll be at the gate.",
      "last_message_at": "2026-08-26T10:30:00Z",
      "unread_count": 1,
      "ride_context": "New Delhi → Jaipur, Aug 28"
    }
  ]
}
```

### 5.2 Get Chat History
**`GET /conversations/conv_777/messages`**
*Requires Auth*

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": "msg_001",
      "sender_id": "usr_12345",
      "content": "Hi Sneha, booking confirmed!",
      "message_type": "text",
      "created_at": "2026-08-26T10:25:00Z"
    },
    {
      "id": "msg_002",
      "sender_id": "usr_987",
      "content": "Great, I'll be at the gate.",
      "message_type": "text",
      "created_at": "2026-08-26T10:30:00Z"
    }
  ]
}
```

---

## 6. Notifications (`/notifications`)

### 6.1 Get Notifications
**`GET /notifications`**
*Requires Auth*

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": "notif_001",
      "type": "booking_request",
      "title": "New Booking Request",
      "body": "Sneha Gupta wants to join your Delhi → Jaipur ride",
      "is_read": false,
      "reference_id": "ride_101",
      "created_at": "2026-08-26T10:15:00Z"
    }
  ]
}
```

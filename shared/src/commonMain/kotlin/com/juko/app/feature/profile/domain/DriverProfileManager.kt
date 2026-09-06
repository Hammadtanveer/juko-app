package com.juko.app.feature.profile.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.juko.app.feature.profile.presentation.VehicleItem

/**
 * Shared singleton managing driver profile completion state across the app.
 */
object DriverProfileManager {

    var fullName by mutableStateOf("Alexander Mitchell")
    var email by mutableStateOf("alex.mitchell@driver.rideshare.com")
    var phoneCountryCode by mutableStateOf("+91")
    var phoneNumber by mutableStateOf("9876543210")
    var isPhoneVerified by mutableStateOf(true)

    // Licence photos (null by default or mock-uploaded)
    var frontLicenceUri by mutableStateOf<String?>(null)
    var backLicenceUri by mutableStateOf<String?>(null)

    // Vehicles list (default has 0 or vehicles)
    var vehicles by mutableStateOf<List<VehicleItem>>(
        listOf(
            VehicleItem(
                id = "veh_1",
                brand = "Toyota",
                model = "Toyota Camry",
                plateNumber = "ABC-1234",
                seatingCapacity = 5,
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCipvSxEU0VgIFtDTAudi-KdkzVxp7Oz24RaZwnz0ymk0LFyGSQst0DnmGAUhwlFc5N6htRJqYVbK8qIuCviGSmyhB2htUW9yalM7GAt4S8Zt7gR3yl-3ASXph0Ju-UqxykJ8ICX2RufyYlD4emhHndoPDhdTieHC4DuRWC7Xj0cJ74Dp3PGwrrFj10NEkRTEoVZx01w-nuUNezMpkfpoXhB7MMRENoHcOCkSIhb8EDxXfOR91SbnID"
            )
        )
    )

    fun getMissingRequirements(): List<String> {
        val missing = mutableListOf<String>()
        if (fullName.isBlank()) {
            missing.add("Full Name")
        }
        if (phoneNumber.length != 10) {
            missing.add("10-Digit Verified Phone Number")
        }
        if (frontLicenceUri == null && backLicenceUri == null) {
            missing.add("Driver Licence Photo")
        }
        if (vehicles.isEmpty()) {
            missing.add("At least 1 Registered Vehicle")
        }
        return missing
    }

    fun isProfileCompleteForPublishing(): Boolean {
        return getMissingRequirements().isEmpty()
    }
}

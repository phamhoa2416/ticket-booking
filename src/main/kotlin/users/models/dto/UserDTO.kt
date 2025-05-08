package users.models.dto

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import users.models.types.UserRole
import java.util.UUID

@Serializable
data class UserCreateDTO(
    @SerialName("username")
    val username: String,

    @SerialName("email")
    val email: String,

    @SerialName("password")
    val password: String,

    @SerialName("phone_number")
    val phoneNumber: String,

    @SerialName("date_of_birth")
    val dateOfBirth: LocalDate? = null,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,

    @SerialName("role")
    val role: UserRole,

    @SerialName("notification_preferences")
    val notificationPreferences: NotificationPreferencesDTO = NotificationPreferencesDTO()
) {
    init {
        require(username.length in 3..50) { "Username must be between 3-50 characters" }
        require(EMAIL_REGEX.matches(email)) { "Invalid email format" }
        require(PASSWORD_REGEX.matches(password)) {
            "Password must contain at least 8 characters, including uppercase, lowercase and numbers"
        }
        phoneNumber.takeIf { it.isNotBlank() }?.let {
            require(PHONE_REGEX.matches(it)) { "Invalid phone number format" }
        }
    }

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)\$".toRegex()
        private val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}\$".toRegex()
        private val PHONE_REGEX = "^[+]?[0-9]{10,15}\$".toRegex()
    }
}

@Serializable
data class UserUpdateDTO(
    val username: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: LocalDate? = null,
    val avatarUrl: String? = null,
    val notificationPreferencesDTO: NotificationPreferencesDTO? = null,
)

@Serializable
data class UserResponseDTO(
    @Contextual val id: UUID,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val dateOfBirth: LocalDate?,
    val avatarUrl: String?,
    val isVerified: Boolean,
    val role: UserRole,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val lastLogin: LocalDateTime?,
    val notificationPreferencesDTO: NotificationPreferencesDTO?,
)

@Serializable
data class NotificationPreferencesDTO(
    val emailNotifications: Boolean = true,
    val pushNotifications: Boolean = true,
    val smsNotifications: Boolean = false,
)
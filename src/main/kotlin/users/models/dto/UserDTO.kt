package users.models.dto

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import users.models.types.PrivacyLevel
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
)

@Serializable
data class UserUpdateDTO(
    @SerialName("username")
    val username: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("phone_number")
    val phoneNumber: String? = null,

    @SerialName("date_of_birth")
    val dateOfBirth: LocalDate? = null,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,
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
)

@Serializable
data class UserPreferencesDTO(
    val language: String = "en",
    val timezone: String = "UTC",
    val notificationEnabled: Boolean = true,
    val emailNotifications: Boolean = true,
    val smsNotifications: Boolean = false,
    val pushNotifications: Boolean = true,
    val theme: String = "light",
    val currency: String = "USD",
    val dateFormat: String = "yyyy-MM-dd",
    val timeFormat: String = "HH:mm",
    val privacyLevel: PrivacyLevel = PrivacyLevel.STANDARD
)
package users.models.types

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    CUSTOMER,
    ORGANIZER,
    ADMIN
}
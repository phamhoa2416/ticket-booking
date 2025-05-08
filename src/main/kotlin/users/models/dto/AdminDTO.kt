package users.models.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import users.models.types.AdminAccessLevel
import kotlinx.datetime.LocalDateTime
import users.models.types.AdminActionStatus
import users.models.types.AdminActionType
import java.util.UUID

@Serializable
data class AdminCreateDTO(
    @Contextual val userId: UUID,
    val accessLevel: AdminAccessLevel,
    val department: String? = null
)

@Serializable
data class AdminUpdateDTO(
    val accessLevel: AdminAccessLevel? = null,
    val department: String? = null
)

@Serializable
data class AdminResponseDTO(
    @Contextual val id: UUID,
    val user: UserResponseDTO,
    val accessLevel: AdminAccessLevel,
    val lastActivity: LocalDateTime?,
    val department: String?,
    val actionHistory: List<AdminActionDTO>?
)

@Serializable
data class AdminActionDTO(
    val type: AdminActionType,
    val targetType: String,
    @Contextual val targetId: UUID?,
    val description: String,
    val performedAt: LocalDateTime,
    val status: AdminActionStatus,
    val notes: String? = null
)
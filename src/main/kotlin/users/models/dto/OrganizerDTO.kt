package users.models.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import users.models.types.VerificationStatus
import java.math.BigDecimal
import java.util.UUID

@Serializable
data class OrganizerCreateDTO(
    @Contextual val userId: UUID,
    val organizationName: String,
    val taxId: String? = null,
    val businessLicense: String? = null,
    val bankAccount: String? = null,
    val contactEmail: String? = null
)

@Serializable
data class OrganizerUpdateDTO(
    val organizationName: String? = null,
    val taxId: String? = null,
    val businessLicense: String? = null,
    val bankAccount: String? = null,
    val contactEmail: String? = null,
    val verificationStatus: VerificationStatus? = null
)

@Serializable
data class OrganizerResponseDTO(
    @Contextual val id: UUID,
    val user: UserResponseDTO,
    val organizationName: String,
    val verificationStatus: VerificationStatus,
    @Contextual val rating: BigDecimal?,
    val totalEvents: Int,
    val contactEmail: String?,
)
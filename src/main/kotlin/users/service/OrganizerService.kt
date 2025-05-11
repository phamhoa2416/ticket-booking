package users.service

import mu.KotlinLogging
import users.exceptions.*
import users.models.dto.*
import users.models.entity.Organizer.rating
import users.models.types.UserRole
import users.models.types.VerificationStatus
import users.repository.OrganizerRepository
import users.repository.UserRepository
import users.utility.*
import java.math.BigDecimal
import java.util.*

private val logger = KotlinLogging.logger {}

class OrganizerService(
    private val userRepository: UserRepository,
    private val organizerRepository: OrganizerRepository,
    private val cacheManager: CacheManager = CacheManager.getInstance()
) {
    suspend fun createOrganizer(organizer: OrganizerCreateDTO): OrganizerResponseDTO {
        logger.info { "Creating new organizer with user ID: ${organizer.userId}" }

        // Validate user exists and has correct role
        val user = userRepository.getUserById(organizer.userId)
        if (user.role != UserRole.ORGANIZER) {
            throw InvalidUserRoleException(organizer.userId, UserRole.ORGANIZER)
        }

        // Validate input
        ValidationUtils.apply {
            validateOrganizationName(organizer.organizationName)
            validateTaxId(organizer.taxId)
            organizer.contactEmail?.let { validateEmail(it) }
            organizer.rating?.let { validateRating(it) }
        }

        return TransactionManager.withTransaction {
            try {
                val createdOrganizer = organizerRepository.createOrganizer(organizer)

                cacheManager.set("organizer:${createdOrganizer.id}", createdOrganizer)

                AuditLogger.logUserAction(
                    userId = createdOrganizer.user.id,
                    userRole = UserRole.ORGANIZER,
                    action = "ORGANIZER_CREATED",
                    details = mapOf(
                        "organizerId" to createdOrganizer.id,
                        "organizationName" to createdOrganizer.organizationName,
                        "verificationStatus" to createdOrganizer.verificationStatus
                    )
                )

                createdOrganizer
            } catch (e: Exception) {
                AuditLogger.logError(
                    userId = organizer.userId,
                    action = "ORGANIZER_CREATION_FAILED",
                    error = e,
                    details = mapOf("organizerData" to organizer.toString())
                )
                throw e
            }
        }
    }

    suspend fun updateOrganizer(organizerId: UUID, organizer: OrganizerUpdateDTO): OrganizerResponseDTO {
        logger.info { "Updating organizer with ID: $organizerId" }

        ValidationUtils.apply {
            organizer.organizationName?.let { validateOrganizationName(it) }
            organizer.taxId?.let { validateTaxId(it) }
            organizer.contactEmail?.let { validateEmail(it) }
            organizer.rating?.let { validateRating(it) }
        }

        return TransactionManager.withTransaction {
            try {
                val existingOrganizer = organizerRepository.getOrganizerById(organizerId)
                    ?: throw UserNotFoundException(organizerId)

                val updatedOrganizer = organizerRepository.updateOrganizer(organizerId, organizer)

                cacheManager.set("organizer:${updatedOrganizer.id}", updatedOrganizer)

                AuditLogger.logUserAction(
                    userId = updatedOrganizer.user.id,
                    userRole = UserRole.ORGANIZER,
                    action = "ORGANIZER_UPDATED",
                    details = mapOf(
                        "organizerId" to organizerId,
                        "updatedFields" to organizer.toString(),
                        "previousStatus" to existingOrganizer.verificationStatus,
                        "newStatus" to updatedOrganizer.verificationStatus
                    )
                )

                updatedOrganizer
            } catch (e: Exception) {
                AuditLogger.logError(
                    userId = null,
                    action = "ORGANIZER_UPDATE_FAILED",
                    error = e,
                    details = mapOf(
                        "organizerId" to organizerId,
                        "updateData" to organizer.toString()
                    )
                )
                throw e
            }
        }
    }

    suspend fun deleteOrganizer(organizerId: UUID): Boolean {
        logger.info { "Deleting organizer with ID: $organizerId" }

        return TransactionManager.withTransaction {
            try {
                val organizer = organizerRepository.getOrganizerById(organizerId)
                    ?: throw UserNotFoundException(organizerId)

                val deleted = organizerRepository.deleteOrganizer(organizerId)

                if (deleted) {
                    cacheManager.remove("organizer:$organizerId")

                    AuditLogger.logUserAction(
                        userId = organizer.user.id,
                        userRole = UserRole.ORGANIZER,
                        action = "ORGANIZER_DELETED",
                        details = mapOf(
                            "organizerId" to organizerId,
                            "organizationName" to organizer.organizationName,
                            "totalEvents" to organizer.totalEvents
                        )
                    )
                }

                deleted
            } catch (e: Exception) {
                AuditLogger.logError(
                    userId = null,
                    action = "ORGANIZER_DELETION_FAILED",
                    error = e,
                    details = mapOf("organizerId" to organizerId)
                )
                throw e
            }
        }
    }

    suspend fun getOrganizerById(organizerId: UUID): OrganizerResponseDTO? {
        logger.info { "Fetching organizer with ID: $organizerId" }

        return cacheManager.getOrSet("organizer:$organizerId") {
            TransactionManager.withReadOnlyTransaction {
                try {
                    organizerRepository.getOrganizerById(organizerId)?.also { organizer ->
                        AuditLogger.logDataAccess(
                            userId = organizer.user.id,
                            userRole = UserRole.ORGANIZER,
                            resourceType = "Organizer",
                            resourceId = organizerId,
                            action = "GET_ORGANIZER"
                        )
                    } ?: throw NoSuchElementException("Organizer not found with ID: $organizerId")
                } catch (e: Exception) {
                    AuditLogger.logError(
                        userId = null,
                        action = "GET_ORGANIZER_FAILED",
                        error = e,
                        details = mapOf("organizerId" to organizerId)
                    )
                    throw e
                }
            }
        }
    }

    suspend fun updateVerificationStatus(
        organizerId: UUID,
        status: VerificationStatus
    ): OrganizerResponseDTO {
        logger.info { "Updating verification status for organizer ID: $organizerId" }

        return TransactionManager.withTransaction {
            try {
                val organizer = organizerRepository.getOrganizerById(organizerId)
                    ?: throw UserNotFoundException(organizerId)

                val updatedOrganizer = organizerRepository.updateOrganizer(
                    organizerId,
                    OrganizerUpdateDTO(verificationStatus = status)
                )

                cacheManager.set("organizer:${updatedOrganizer.id}", updatedOrganizer)

                AuditLogger.logUserAction(
                    userId = updatedOrganizer.user.id,
                    userRole = UserRole.ORGANIZER,
                    action = "VERIFICATION_STATUS_UPDATED",
                    details = mapOf(
                        "organizerId" to organizerId,
                        "previousRating" to (organizer.rating ?: BigDecimal.ZERO),
                        "newRating" to rating,
                    )
                )

                updatedOrganizer
            } catch (e: Exception) {
                AuditLogger.logError(
                    userId = null,
                    action = "VERIFICATION_STATUS_UPDATE_FAILED",
                    error = e,
                    details = mapOf(
                        "organizerId" to organizerId,
                        "status" to status
                    )
                )
                throw e
            }
        }
    }

    suspend fun updateRating(
        organizerId: UUID,
        rating: BigDecimal
    ): OrganizerResponseDTO {
        logger.info { "Updating rating for organizer ID: $organizerId" }

        ValidationUtils.validateRating(rating)

        return TransactionManager.withTransaction {
            try {
                val organizer = organizerRepository.getOrganizerById(organizerId)
                    ?: throw UserNotFoundException(organizerId)

                val updatedOrganizer = organizerRepository.updateOrganizer(
                    organizerId,
                    OrganizerUpdateDTO(rating = rating)
                )

                cacheManager.set("organizer:${updatedOrganizer.id}", updatedOrganizer)

                AuditLogger.logUserAction(
                    userId = updatedOrganizer.user.id,
                    userRole = UserRole.ORGANIZER,
                    action = "RATING_UPDATED",
                    details = mapOf(
                        "organizerId" to organizerId,
                        "previousRating" to (organizer.rating ?: BigDecimal.ZERO),
                        "newRating" to rating,
                        "ratingDifference" to rating.subtract(organizer.rating ?: BigDecimal.ZERO)
                    )
                )

                updatedOrganizer
            } catch (e: Exception) {
                AuditLogger.logError(
                    userId = null,
                    action = "RATING_UPDATE_FAILED",
                    error = e,
                    details = mapOf(
                        "organizerId" to organizerId,
                        "rating" to rating
                    )
                )
                throw e
            }
        }
    }
}
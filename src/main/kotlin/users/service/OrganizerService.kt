package users.service

import users.models.dto.OrganizerCreateDTO
import users.models.dto.OrganizerResponseDTO
import users.models.dto.OrganizerUpdateDTO
import users.models.types.UserRole
import users.repository.OrganizerRepository
import users.repository.UserRepository
import java.util.*

private val logger = mu.KotlinLogging.logger {}

class OrganizerService (
    private val userRepository: UserRepository,
    private val organizerRepository: OrganizerRepository
) {
    suspend fun createOrganizer(organizer: OrganizerCreateDTO): OrganizerResponseDTO {
        logger.info { "Creating new organizer with user id: ${organizer.userId}" }

        val user = userRepository.getUserById(organizer.userId)

        if (user.role != UserRole.ORGANIZER) {
            logger.error { "User with ID ${organizer.userId} is not an organizer." }
            throw IllegalArgumentException("User must have ADMIN role")
        }

        return try {
            organizerRepository.createOrganizer(organizer).also {
                logger.info { "Organizer created successfully with ID: ${it.id}" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to create organizer" }
            throw e
        }
    }

    suspend fun updateOrganizer(organizerId: UUID, organizer: OrganizerUpdateDTO): OrganizerResponseDTO {
        logger.info { "Updating organizer with ID: $organizerId" }

        return try {
            organizerRepository.updateOrganizer(organizerId, organizer).also {
                logger.info { "Organizer updated successfully with ID: ${it.id}" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to update organizer" }
            throw e
        }
    }

    suspend fun deleteOrganizer(organizerId: UUID): Boolean {
        logger.info { "Deleting organizer with ID: $organizerId" }

        return try {
            organizerRepository.deleteOrganizer(organizerId).also {
                logger.info { "Organizer deleted successfully with ID: $organizerId" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to delete organizer" }
            throw e
        }
    }

    suspend fun getAllOrganizers(): List<OrganizerResponseDTO> {
        logger.info { "Fetching all organizers" }

        return try {
            organizerRepository.getAllOrganizers().also {
                logger.info { "Fetched ${it.size} organizers" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch organizers" }
            throw e
        }
    }

    suspend fun getOrganizerById(organizerId: UUID): OrganizerResponseDTO? {
        logger.info { "Fetching organizer with ID: $organizerId" }

        return try {
            organizerRepository.getOrganizerById(organizerId).also {
                logger.info { "Fetched organizer with ID: $organizerId" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch organizer" }
            throw e
        }
    }

    suspend fun getOrganizerByUserId(userId: UUID): OrganizerResponseDTO? {
        logger.info { "Fetching organizer with user ID: $userId" }

        return try {
            organizerRepository.getOrganizerByUserId(userId).also {
                logger.info { "Fetched organizer with user ID: $userId" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch organizer" }
            throw e
        }
    }

    suspend fun getOrganizerByEmail(email: String): OrganizerResponseDTO? {
        logger.info { "Fetching organizer with email: $email" }

        return try {
            organizerRepository.getOrganizerByEmail(email).also {
                logger.info { "Fetched organizer with email: $email" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch organizer" }
            throw e
        }
    }

    suspend fun getOrganizationByName(organizationName: String): OrganizerResponseDTO? {
        logger.info { "Fetching organization with name: $organizationName" }

        return try {
            organizerRepository.getOrganizationByName(organizationName).also {
                logger.info { "Fetched organization with name: $organizationName" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch organization" }
            throw e
        }
    }
}
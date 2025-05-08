package users.repository

import users.models.dto.OrganizerCreateDTO
import users.models.dto.OrganizerResponseDTO
import users.models.dto.OrganizerUpdateDTO
import java.util.UUID

interface OrganizerRepository {
    suspend fun createOrganizer(organizer: OrganizerCreateDTO): OrganizerResponseDTO
    suspend fun updateOrganizer(organizerId: UUID, organizer: OrganizerUpdateDTO): OrganizerResponseDTO
    suspend fun deleteOrganizer(organizerId: UUID): Boolean
    suspend fun getAllOrganizers(): List<OrganizerResponseDTO>
    suspend fun getOrganizerById(organizerId: UUID): OrganizerResponseDTO?
    suspend fun getOrganizerByUserId(userId: UUID): OrganizerResponseDTO?
    suspend fun getOrganizerByEmail(email: String): OrganizerResponseDTO?
    suspend fun getOrganizationByName(organizationName: String): OrganizerResponseDTO?
}
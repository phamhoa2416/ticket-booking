package users.repository

import org.jetbrains.exposed.sql.transactions.transaction
import users.models.dto.OrganizerCreateDTO
import users.models.dto.OrganizerResponseDTO
import users.models.dto.OrganizerUpdateDTO
import users.models.entity.Organizer
import users.models.entity.OrganizerEntity
import users.models.entity.User
import users.models.entity.UserEntity
import users.utils.UserUtility
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

class OrganizerRepositoryImpl: OrganizerRepository {
    override suspend fun createOrganizer(organizer: OrganizerCreateDTO): OrganizerResponseDTO = transaction {
        val user = UserEntity.findById(organizer.userId) ?: throw NoSuchElementException("User not found")

        val organizerEntity = OrganizerEntity.new {
            this.user = user
            this.organizationName = organizer.organizationName
            this.contactEmail = organizer.contactEmail
            this.taxId = organizer.taxId
            this.businessLicense = organizer.businessLicense
            this.bankAccount = organizer.bankAccount
            this.verificationStatus = organizer.verificationStatus
            this.rating = organizer.rating
            this.totalEvents = organizer.totalEvents ?: 0
        }

        UserUtility.toOrganizeResponseDTO(organizerEntity)
    }

    override suspend fun updateOrganizer(organizerId: UUID, organizer: OrganizerUpdateDTO): OrganizerResponseDTO = transaction {
        val organizerEntity = OrganizerEntity.findById(organizerId) ?: throw NoSuchElementException("Organizer not found")

        organizerEntity.organizationName.let { organizerEntity.organizationName = it }
        organizerEntity.taxId = organizer.taxId ?: organizerEntity.taxId
        organizerEntity.businessLicense = organizer.businessLicense ?: organizerEntity.businessLicense
        organizerEntity.bankAccount = organizer.bankAccount ?: organizerEntity.bankAccount
        organizerEntity.contactEmail = organizer.contactEmail ?: organizerEntity.contactEmail
        organizerEntity.verificationStatus = organizer.verificationStatus ?: organizerEntity.verificationStatus
        organizerEntity.rating = organizer.rating ?: organizerEntity.rating
        organizerEntity.totalEvents = organizer.totalEvents ?: organizerEntity.totalEvents

        UserUtility.toOrganizeResponseDTO(organizerEntity)
    }

    override suspend fun deleteOrganizer(organizerId: UUID): Boolean = transaction {
        val organizerEntity = OrganizerEntity.findById(organizerId) ?: return@transaction false
        organizerEntity.delete()
        true
    }

    override suspend fun getAllOrganizers(): List<OrganizerResponseDTO> = transaction {
        OrganizerEntity.all().map { UserUtility.toOrganizeResponseDTO(it) }
    }

    override suspend fun getOrganizerById(organizerId: UUID): OrganizerResponseDTO? = transaction {
        OrganizerEntity.findById(organizerId)?.let { UserUtility.toOrganizeResponseDTO(it) }
    }

    override suspend fun getOrganizerByUserId(userId: UUID): OrganizerResponseDTO? = transaction {
        OrganizerEntity.find { Organizer.organizerId eq userId }.firstOrNull()?.let {
            UserUtility.toOrganizeResponseDTO(it)
        }
    }

    override suspend fun getOrganizerByEmail(email: String): OrganizerResponseDTO? = transaction {
        val user = UserEntity.find { User.email eq email }.firstOrNull() ?: return@transaction null
        OrganizerEntity.find { Organizer.organizerId eq user.id }.firstOrNull()?.let {
            UserUtility.toOrganizeResponseDTO(it)
        }
    }

    override suspend fun getOrganizationByName(organizationName: String): OrganizerResponseDTO? = transaction {
        OrganizerEntity.find { Organizer.organizationName eq organizationName }.firstOrNull()?.let {
            UserUtility.toOrganizeResponseDTO(it)
        }
    }
}
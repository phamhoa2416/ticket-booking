package users.models.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import users.models.types.VerificationStatus
import java.util.UUID

object Organizer : UUIDTable() {
    val organizerId = reference("organizer_id", User).uniqueIndex()
    val organizationName = varchar("organization_name", 255)
    val contactEmail = varchar("contact_email", 255).nullable()
    val taxId = varchar("tax_id", 50).nullable()
    val businessLicense = text("business_license").nullable()
    val bankAccount = text("bank_account").nullable()
    // Store only a reference to a secure payment processor or encrypt the field
    val verificationStatus = enumerationByName("verification_status", 20,
                                                VerificationStatus::class).default(VerificationStatus.PENDING)
    val rating = decimal("rating", 3, 1).nullable()
    val totalEvents = integer("total_events").default(0)
}

class OrganizerEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<OrganizerEntity>(Organizer)

    var user by UserEntity referencedOn Organizer.organizerId
    var organizationName by Organizer.organizationName
    var taxId by Organizer.taxId
    var contactEmail by Organizer.contactEmail
    var businessLicense by Organizer.businessLicense
    var bankAccount by Organizer.bankAccount
    var verificationStatus by Organizer.verificationStatus
    var rating by Organizer.rating
    var totalEvents by Organizer.totalEvents

    // Relationships
}
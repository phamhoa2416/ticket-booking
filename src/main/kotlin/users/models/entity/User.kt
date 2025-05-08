package users.models.entity

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import users.models.types.UserRole
import java.util.UUID

object User : UUIDTable() {
    val username = varchar("name", 255).index()
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val phoneNumber = varchar("phone_number", 20)
    val dateOfBirth = date("date_of_birth").nullable()
    val avatarUrl = text("avatar_url").nullable()
    val isVerified = bool("is_verified").default(false)
    val isDeleted = bool("is_deleted").default(false)
    val role = enumerationByName("role", 20, UserRole::class)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at").nullable()
    val lastLogin = datetime("last_login").nullable()
}

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(User)

    var username by User.username
    var email by User.email
    var password by User.password
    var phoneNumber by User.phoneNumber
    var dateOfBirth by User.dateOfBirth
    var avatarUrl by User.avatarUrl
    var isVerified by User.isVerified
    var isDeleted by User.isDeleted
    var role by User.role
    var createdAt by User.createdAt
    var updatedAt by User.updatedAt
    var lastLogin by User.lastLogin

    // Relationships
    val customer by CustomerEntity referrersOn Customer.customerId
    val organizer by OrganizerEntity referrersOn Organizer.organizerId
    val admin by AdminEntity referrersOn Admin.adminId
}
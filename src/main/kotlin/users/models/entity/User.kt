package users.models.entity

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import users.models.types.UserRole
import java.util.UUID

object Users : UUIDTable() {
    val username = varchar("name", 255).index()
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val phoneNumber = varchar("phone_number", 20)
    val dateOfBirth = date("date_of_birth").nullable()
    val avatarUrl = text("avatar_url").nullable()
    val isVerified = bool("is_verified").default(false)
    val role = enumerationByName("role", 20, UserRole::class)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at").nullable()
    val lastLogin = datetime("last_login").nullable()
}

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(Users)

    var username by Users.username
    var email by Users.email
    var password by Users.password
    var phoneNumber by Users.phoneNumber
    var dateOfBirth by Users.dateOfBirth
    var avatarUrl by Users.avatarUrl
    var isVerified by Users.isVerified
    var role by Users.role
    var createdAt by Users.createdAt
    var updatedAt by Users.updatedAt
    var lastLogin by Users.lastLogin

    // Relationships
    val customer by CustomerEntity referrersOn Customers.customerId
    val organizer by OrganizerEntity referrersOn Organizer.organizerId
    val admin by AdminEntity referrersOn Admin.adminId
}
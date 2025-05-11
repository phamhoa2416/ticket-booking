package users.models.entity

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import users.models.types.PrivacyLevel
import users.models.types.UserRole
import java.util.UUID

abstract class VersionedTable : UUIDTable() {
    val version = long("version").default(0)
}

abstract class VersionedEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    abstract var version: Long

    fun incrementVersion() {
        version++
    }

    fun checkVersion(expectedVersion: Long) {
        if (version != expectedVersion) {
            throw ConcurrentModificationException(
                "Concurrent modification detected. Expected version $expectedVersion but found $version"
            )
        }
    }
}

object User : VersionedTable() {
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

class UserEntity(id: EntityID<UUID>) : VersionedEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(User)

    override var version by User.version
    var username by User.username
    var email by User.email
    var password by User.password
    var phoneNumber by User.phoneNumber
    var dateOfBirth by User.dateOfBirth
    var avatarUrl by User.avatarUrl
    var isVerified by User.isVerified
    var role by User.role
    var createdAt by User.createdAt
    var updatedAt by User.updatedAt
    var lastLogin by User.lastLogin

    // Relationships
    val customer by CustomerEntity referrersOn Customer.customerId
    val organizer by OrganizerEntity referrersOn Organizer.organizerId
    val admin by AdminEntity referrersOn Admin.adminId
}

object UserPreferences: UUIDTable() {
    val userId = reference("user_id", User).uniqueIndex()
    val language = varchar("language", 20).default("en")
    val timezone = varchar("timezone", 20).default("UTC")
    val notificationEnabled = bool("notification_enabled").default(true)
    val emailNotifications = bool("email_notifications").default(true)
    val smsNotifications = bool("sms_notifications").default(false)
    val pushNotifications = bool("push_notifications").default(true)
    val theme = varchar("theme", 20).default("light")
    val currency = varchar("currency", 3).default("USD")
    val dateFormat = varchar("date_format", 20).default("yyyy-MM-dd")
    val timeFormat = varchar("time_format", 20).default("HH:mm")
    val privacyLevel = enumerationByName("privacy_level", 20, PrivacyLevel::class).default(PrivacyLevel.STANDARD)
}

class UserPreferencesEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserPreferencesEntity>(UserPreferences)

    var user by UserEntity referencedOn UserPreferences.userId
    var language by UserPreferences.language
    var timezone by UserPreferences.timezone
    var notificationEnabled by UserPreferences.notificationEnabled
    var emailNotifications by UserPreferences.emailNotifications
    var smsNotifications by UserPreferences.smsNotifications
    var pushNotifications by UserPreferences.pushNotifications
    var theme by UserPreferences.theme
    var currency by UserPreferences.currency
    var dateFormat by UserPreferences.dateFormat
    var timeFormat by UserPreferences.timeFormat
    var privacyLevel by UserPreferences.privacyLevel
}
package users.models.entity

import users.models.types.AdminAccessLevel
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.UUID

object Admin : UUIDTable() {
    val adminId = reference("admin_id", Users).uniqueIndex()
    val accessLevel = enumerationByName("access_level", 20, AdminAccessLevel::class)
    val lastActivity = datetime("last_activity").nullable()
    val department = varchar("department", 255).nullable()
}

class AdminEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AdminEntity>(Admin)

    var user by UserEntity referencedOn Admin.adminId
    var accessLevel by Admin.accessLevel
    var lastActivity by Admin.lastActivity
    var department by Admin.department
    // Relationships
}
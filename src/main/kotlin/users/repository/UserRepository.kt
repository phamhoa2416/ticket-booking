package users.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import users.models.dto.UserCreateDTO
import users.models.dto.UserResponseDTO
import users.models.dto.UserUpdateDTO
import users.models.entity.User
import users.models.entity.UserEntity
import users.utils.toJavaLocalDateTime
import users.utils.toKotlinxDateTime
import users.utils.toKotlinxLocalDate
import java.util.NoSuchElementException
import java.util.UUID

interface UserRepository {
    suspend fun createUser(user: UserCreateDTO): UserResponseDTO
    suspend fun updateUser(userId: UUID, user: UserUpdateDTO): UserResponseDTO
    suspend fun deleteUser(userId: UUID)
    suspend fun getUserById(id: UUID): UserResponseDTO
    suspend fun getUserByUsername(username: String): UserResponseDTO?
    suspend fun getUserByEmail(email: String): UserResponseDTO?
    suspend fun existsByUsername(username: String): Boolean
    suspend fun existsByEmail(email: String): Boolean
    suspend fun findAll(page: Int, pageSize: Int): List<UserResponseDTO>
}

class UserRepositoryImpl : UserRepository {
    override suspend fun createUser(user: UserCreateDTO): UserResponseDTO = transaction {
        if (UserEntity.find { User.email eq user.email }.count() > 0)
            throw IllegalArgumentException("Email already exists")

        UserEntity.new {
            username = user.username
            email = user.email
            password = user.password
            phoneNumber = user.phoneNumber
            dateOfBirth = user.dateOfBirth?.toJavaLocalDate()
            avatarUrl = user.avatarUrl
            role = user.role
        }.toUserResponseDTO()
    }

    override suspend fun updateUser(userId: UUID, user: UserUpdateDTO): UserResponseDTO = transaction {
        val existUser = UserEntity.findById(userId)
            ?: throw NoSuchElementException("User with ID $userId not found")

        user.username?.let { existUser.username = it }
        user.email?.let { existUser.email = it }
        user.phoneNumber?.let { existUser.phoneNumber = it }
        user.dateOfBirth?.let { existUser.dateOfBirth = it.toJavaLocalDate() }
        user.avatarUrl?.let { existUser.avatarUrl = it }
        existUser.updatedAt = Clock.System.now().toLocalDateTime(TimeZone.UTC).toJavaLocalDateTime()

        existUser.toUserResponseDTO()
    }

    override suspend fun deleteUser(userId: UUID) = transaction {
        UserEntity.findById(userId)?.delete() ?: throw NoSuchElementException("User with ID $userId not found")
    }

    override suspend fun getUserById(id: UUID): UserResponseDTO = transaction {
        UserEntity.findById(id)?.toUserResponseDTO()
            ?: throw NoSuchElementException("User with ID $id not found")
    }

    override suspend fun getUserByUsername(username: String): UserResponseDTO? = transaction {
        UserEntity.find { User.username eq username }.firstOrNull()?.toUserResponseDTO()
    }

    override suspend fun getUserByEmail(email: String): UserResponseDTO? = transaction {
        UserEntity.find { User.email eq email }.firstOrNull()?.toUserResponseDTO()
    }

    override suspend fun existsByUsername(username: String): Boolean = transaction {
        !UserEntity.find { User.username eq username }.empty()
    }

    override suspend fun existsByEmail(email: String): Boolean = transaction {
        !UserEntity.find { User.email eq email }.empty()
    }

    override suspend fun findAll(page: Int, pageSize: Int): List<UserResponseDTO> = transaction {
        UserEntity.all()
            .orderBy(User.createdAt to SortOrder.DESC)
            .limit(pageSize)
            .offset((page - 1) * pageSize.toLong())
            .map { it.toUserResponseDTO() }
    }

    private fun UserEntity.toUserResponseDTO(): UserResponseDTO {
        return UserResponseDTO(
            id = id.value,
            email = email,
            username = username,
            phoneNumber = phoneNumber,
            dateOfBirth = dateOfBirth?.toKotlinxLocalDate(),
            avatarUrl = avatarUrl,
            role = role,
            isVerified = isVerified,
            createdAt = createdAt.toKotlinxDateTime(),
            updatedAt = updatedAt?.toKotlinxDateTime(),
            lastLogin = lastLogin?.toKotlinxDateTime(),
        )
    }
}
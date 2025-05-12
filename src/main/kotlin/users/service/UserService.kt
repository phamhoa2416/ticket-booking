package users.service

import mu.KotlinLogging
import kotlinx.datetime.toJavaLocalDate
import org.mindrot.jbcrypt.BCrypt
import users.exceptions.DuplicateResourceException
import users.models.dto.UserCreateDTO
import users.models.dto.UserResponseDTO
import users.models.dto.UserUpdateDTO
import users.repository.UserRepository
import users.utility.*
import java.util.*

private val logger = KotlinLogging.logger {}

class UserService(
    private val userRepository: UserRepository,
    private val cacheManager: CacheManager = CacheManager.getInstance()
) {
    suspend fun createUser(user: UserCreateDTO): UserResponseDTO {
        logger.info { "Creating new user with email: ${user.email}" }

        ValidationUtils.apply {
            validateEmail(user.email)
            validatePassword(user.password)
            validateUsername(user.username)
            validatePhoneNumber(user.phoneNumber)
            validateDateOfBirth(user.dateOfBirth?.toJavaLocalDate())
        }

        if (userRepository.existsByEmail(user.email)) {
            logger.warn { "Email ${user.email} already exists" }
            throw DuplicateResourceException("User", user.email)
        }

        return TransactionManager.withTransaction {
            try {
                val hashedUser = user.copy(
                    password = BCrypt.hashpw(user.password, BCrypt.gensalt())
                )
                val createdUser = userRepository.createUser(hashedUser)

                cacheManager.set(
                    key = "user:${createdUser.id}",
                    value = createdUser,
                )

                AuditLogger.logUserAction(
                    userId = createdUser.id,
                    userRole = createdUser.role,
                    action = "USER_CREATED",
                    details = mapOf(
                        "email" to createdUser.email,
                        "username" to createdUser.username,
                        "role" to createdUser.role,
                    )
                )

                createdUser
            } catch (e: Exception) {
                AuditLogger.logError(
                    userId = null,
                    action = "USER_CREATION_FAILED",
                    error = e,
                    details = mapOf("email" to user.email)
                )
                throw e
            }
        }
    }

    suspend fun updateUser(userId: UUID, user: UserUpdateDTO): UserResponseDTO {
        logger.info { "Updating user with ID: $userId" }

        ValidationUtils.apply {
            user.email?.let { validateEmail(it) }
            user.username?.let { validateUsername(it) }
            user.phoneNumber?.let { validatePhoneNumber(it) }
            user.dateOfBirth?.let { validateDateOfBirth(it.toJavaLocalDate()) }
        }

        return TransactionManager.withTransaction {
            try {
                val existingUser = userRepository.getUserById(userId)

                user.email?.let { newEmail ->
                    if (newEmail != existingUser.email && userRepository.existsByEmail(newEmail)) {
                        throw DuplicateResourceException("User", newEmail)
                    }
                }

                val updatedUser = userRepository.updateUser(userId, user)

                cacheManager.set(
                    key = "user:${updatedUser.id}",
                    value = updatedUser,
                )

                AuditLogger.logUserAction(
                    userId = updatedUser.id,
                    userRole = updatedUser.role,
                    action = "USER_UPDATED",
                    details = mapOf(
                        "updatedFields" to user.toString(),
                        "previousEmail" to existingUser.email,
                        "newEmail" to updatedUser.email
                    )
                )

                updatedUser
            } catch (e: Exception) {
                AuditLogger.logError(
                    userId = userId,
                    action = "USER_UPDATE_FAILED",
                    error = e,
                    details = mapOf("userId" to userId.toString())
                )
                throw e
            }
        }
    }

    suspend fun deleteUser(userId: UUID) {
        logger.info { "Deleting user with ID: $userId" }

        TransactionManager.withTransaction {
            try {
                val user = userRepository.getUserById(userId)
                userRepository.deleteUser(userId)

                cacheManager.remove(key = "user:${user.id}")

                AuditLogger.logUserAction(
                    userId = userId,
                    userRole = user.role,
                    action = "USER_DELETED",
                    details = mapOf(
                        "email" to user.email,
                        "username" to user.username,
                    )
                )
            } catch (e: Exception) {
                AuditLogger.logError(
                    userId = userId,
                    action = "USER_DELETION_FAILED",
                    error = e,
                    details = emptyMap()
                )
                throw e
            }
        }
    }

    suspend fun getUserById(id: UUID): UserResponseDTO {
        logger.info { "Fetching user with ID: $id" }

        return cacheManager.getOrSet("user:$id") {
            TransactionManager.withTransaction {
                try {
                    userRepository.getUserById(id).also { user ->
                        AuditLogger.logDataAccess(
                            userId = id,
                            userRole = user.role,
                            resourceType = "User",
                            resourceId = id,
                            action = "GET_USER"
                        )
                    }
                } catch (e: Exception) {
                    AuditLogger.logError(
                        userId = id,
                        action = "GET_USER_FAILED",
                        error = e,
                        details = emptyMap()
                    )
                    throw e
                }
            }
        }
    }

    suspend fun getUserByUsername(username: String): UserResponseDTO? {
        logger.info { "Fetching user with username: $username" }

        return cacheManager.getOrSet("user:username:$username") {
            TransactionManager.withTransaction {
                try {
                    userRepository.getUserByUsername(username)?.also { user ->
                        AuditLogger.logDataAccess(
                            userId = user.id,
                            userRole = user.role,
                            resourceType = "User",
                            resourceId = user.id,
                            action = "GET_USER_BY_USERNAME"
                        )
                    } ?: throw NoSuchElementException("User not found with username: $username")
                } catch (e: Exception) {
                    AuditLogger.logError(
                        userId = null,
                        action = "GET_USER_BY_USERNAME_FAILED",
                        error = e,
                        details = mapOf("username" to username)
                    )
                    throw e
                }
            }
        }
    }

    suspend fun getUserByEmail(email: String): UserResponseDTO? {
        logger.info { "Fetching user with email: $email" }
        return try {
            cacheManager.getOrSet("user:email:$email") {
                userRepository.getUserByEmail(email)?.also { user ->
                    AuditLogger.logDataAccess(
                        userId = user.id,
                        userRole = user.role,
                        resourceType = "User",
                        resourceId = user.id,
                        action = "GET_USER_BY_EMAIL"
                    )
                } ?: throw NoSuchElementException("User not found with email: $email")
            }
        } catch (e: Exception) {
            AuditLogger.logError(
                userId = null,
                action = "GET_USER_BY_EMAIL_FAILED",
                error = e,
                details = mapOf("email" to email)
            )
            throw e
        }
    }

    suspend fun findAll(page: Int, pageSize: Int): List<UserResponseDTO> {
        logger.info { "Fetching users page: $page with size: $pageSize" }
        return try {
            cacheManager.getOrSet("users:page:$page:size:$pageSize") {
                userRepository.findAll(page, pageSize).also {
                    logger.info { "Successfully fetched ${it.size} users" }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch users" }
            throw e
        }
    }
}
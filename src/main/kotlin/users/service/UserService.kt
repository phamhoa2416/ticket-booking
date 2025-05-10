package users.service

import mu.KotlinLogging
import kotlinx.datetime.toJavaLocalDate
import org.mindrot.jbcrypt.BCrypt
import users.models.dto.UserCreateDTO
import users.models.dto.UserResponseDTO
import users.models.dto.UserUpdateDTO
import users.repository.UserRepository
import users.utils.UserUtility
import java.util.*

private val logger = KotlinLogging.logger {}

class UserService(private val userRepository: UserRepository) {
    suspend fun createUser(user: UserCreateDTO): UserResponseDTO {
        logger.info { "Creating new user with email: ${user.email}" }
        UserUtility.validateUserCreateDTO(
            email = user.email,
            password = user.password,
            username = user.username,
            phoneNumber = user.phoneNumber,
            dateOfBirth = user.dateOfBirth?.toJavaLocalDate()
        )
        val hashedUser = user.copy(password = BCrypt.hashpw(user.password, BCrypt.gensalt()))

        if (userRepository.existsByEmail(hashedUser.email)) {
            logger.warn { "Email ${hashedUser.email} already exists" }
            throw IllegalArgumentException("Email ${hashedUser.email} already exists")
        }

        return try {
            userRepository.createUser(hashedUser).also {
                logger.info { "Successfully created user with ID: ${it.id}" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to create user with email: ${user.email}" }
            throw e
        }
    }

    suspend fun updateUser(userId: UUID, user: UserUpdateDTO): UserResponseDTO {
        logger.info { "Updating user with ID: $userId" }
        UserUtility.validateUserUpdateDTO(
            email = user.email,
            username = user.username,
            phoneNumber = user.phoneNumber,
            dateOfBirth = user.dateOfBirth?.toJavaLocalDate()
        )

        user.email?.let {
            if (it != userRepository.getUserById(userId).email && userRepository.existsByEmail(it)) {
                logger.warn { "Email $it already exists" }
                throw IllegalArgumentException("Email $it already exists")
            }
        }

        return try {
            userRepository.updateUser(userId, user).also {
                logger.info { "Successfully updated user with ID: $userId" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to update user with ID: $userId" }
            throw e
        }
    }

    suspend fun deleteUser(userId: UUID) {
        logger.info { "Deleting user with ID: $userId" }
        try {
            userRepository.deleteUser(userId)
            logger.info { "Successfully deleted user with ID: $userId" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to delete user with ID: $userId" }
            throw e
        }
    }

    suspend fun getUserById(id: UUID): UserResponseDTO {
        logger.info { "Fetching user with ID: $id" }
        return try {
            userRepository.getUserById(id).also {
                logger.info { "Successfully fetched user with ID: $id" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch user with ID: $id" }
            throw e
        }
    }

    suspend fun getUserByUsername(username: String): UserResponseDTO? {
        logger.info { "Fetching user with username: $username" }
        return try {
            userRepository.getUserByUsername(username).also {
                logger.info { "Successfully fetched user with username: $username" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch user with username: $username" }
            throw e
        }
    }

    suspend fun getUserByEmail(email: String): UserResponseDTO? {
        logger.info { "Fetching user with email: $email" }
        return try {
            userRepository.getUserByEmail(email).also {
                logger.info { "Successfully fetched user with email: $email" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch user with email: $email" }
            throw e
        }
    }

    suspend fun findAll(page: Int, pageSize: Int): List<UserResponseDTO> {
        logger.info { "Fetching users page: $page with size: $pageSize" }
        return try {
            userRepository.findAll(page, pageSize).also {
                logger.info { "Successfully fetched ${it.size} users" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch users" }
            throw e
        }
    }
}
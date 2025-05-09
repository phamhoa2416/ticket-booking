package users.service

import mu.KotlinLogging
import kotlinx.datetime.toJavaLocalDate
import org.mindrot.jbcrypt.BCrypt
import users.models.dto.UserCreateDTO
import users.models.dto.UserResponseDTO
import users.models.dto.UserUpdateDTO
import users.repository.UserRepository
import java.time.LocalDate
import java.util.*

private val logger = KotlinLogging.logger {}

class UserService(private val userRepository: UserRepository) {
    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private const val MAX_PASSWORD_LENGTH = 100
        private const val MIN_USERNAME_LENGTH = 3
        private const val MAX_USERNAME_LENGTH = 50
        private val PASSWORD_PATTERN = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
        private val USERNAME_PATTERN = Regex("^[a-zA-Z0-9_-]{3,50}$")
        private val PHONE_PATTERN = Regex("^\\+?[1-9]\\d{1,14}$")
        private val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@(.+)$")
    }

    suspend fun createUser(user: UserCreateDTO): UserResponseDTO {
        logger.info { "Creating new user with email: ${user.email}" }
        validateUserCreateDTO(user)
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
        validateUserUpdateDTO(user)

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

    private fun validateUserCreateDTO(user: UserCreateDTO) {
        validateEmail(user.email)
        validatePassword(user.password)
        validateUsername(user.username)
        validatePhoneNumber(user.phoneNumber)
        validateDateOfBirth(user.dateOfBirth?.toJavaLocalDate())
    }

    private fun validateUserUpdateDTO(user: UserUpdateDTO) {
        user.email?.let { validateEmail(it) }
        user.username?.let { validateUsername(it) }
        user.phoneNumber?.let { validatePhoneNumber(it) }
        user.dateOfBirth?.let { validateDateOfBirth(it.toJavaLocalDate()) }
    }

    private fun validateEmail(email: String) {
        require(email.matches(EMAIL_PATTERN)) { "Invalid email format" }
    }

    private fun validatePassword(password: String) {
        require(password.length in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH) { 
            "Password must be between $MIN_PASSWORD_LENGTH and $MAX_PASSWORD_LENGTH characters" 
        }
        require(password.matches(PASSWORD_PATTERN)) { 
            "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character" 
        }
    }

    private fun validateUsername(username: String) {
        require(username.length in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH) { 
            "Username must be between $MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH characters" 
        }
        require(username.matches(USERNAME_PATTERN)) { 
            "Username can only contain letters, numbers, underscores, and hyphens" 
        }
    }

    private fun validatePhoneNumber(phoneNumber: String?) {
        phoneNumber?.let {
            require(it.matches(PHONE_PATTERN)) { "Invalid phone number format" }
        }
    }

    private fun validateDateOfBirth(dateOfBirth: LocalDate?) {
        dateOfBirth?.let {
            require(it.isBefore(LocalDate.now())) { "Date of birth cannot be in the future" }
            require(it.isAfter(LocalDate.now().minusYears(150))) { "Invalid date of birth" }
        }
    }
}
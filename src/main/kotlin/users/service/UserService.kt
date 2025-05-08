package users.service

import org.mindrot.jbcrypt.BCrypt
import users.models.dto.UserCreateDTO
import users.models.dto.UserResponseDTO
import users.models.dto.UserUpdateDTO
import users.repository.UserRepository
import java.util.*

class UserService(private val userRepository: UserRepository) {
    suspend fun createUser(user: UserCreateDTO): UserResponseDTO {
        require(user.email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))) { "Invalid email format" }
        require(user.password.length >= 8) { "Password must be at least 8 characters" }

        val hashedUser = user.copy(password = BCrypt.hashpw(user.password, BCrypt.gensalt()))

        if (userRepository.existsByEmail(hashedUser.email)) {
            throw IllegalArgumentException("Email ${hashedUser.email} already exists")
        }
        return userRepository.createUser(hashedUser)
    }

    suspend fun updateUser(userId: UUID, user: UserUpdateDTO): UserResponseDTO {
        user.email?.let {
            require(it.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))) { "Invalid email format" }
            // Check email uniqueness for new email
            if (it != userRepository.getUserById(userId).email && userRepository.existsByEmail(it)) {
                throw IllegalArgumentException("Email $it already exists")
            }
        }

        return userRepository.updateUser(userId, user)
    }

    suspend fun deleteUser(userId: UUID) {
        userRepository.deleteUser(userId)
    }

    suspend fun getUserById(id: UUID): UserResponseDTO {
        return userRepository.getUserById(id)
    }

    suspend fun getUserByUsername(username: String): UserResponseDTO? {
        return userRepository.getUserByUsername(username)
    }

    suspend fun getUserByEmail(email: String): UserResponseDTO? {
        return userRepository.getUserByEmail(email)
    }

    suspend fun existsByUsername(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    suspend fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }
}
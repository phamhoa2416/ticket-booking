package users.repository

import users.models.dto.UserCreateDTO
import users.models.dto.UserResponseDTO
import users.models.dto.UserUpdateDTO
import java.util.UUID

interface UserRepository {
    suspend fun createUser(user: UserCreateDTO): UserResponseDTO
    suspend fun updateUser(userId: UUID, user: UserUpdateDTO): UserResponseDTO
    suspend fun deleteUser(userId: UUID): Boolean
    suspend fun getUserById(id: UUID): UserResponseDTO
    suspend fun getUserByEmail(email: String): UserResponseDTO?
    suspend fun existsByUsername(username: String): Boolean
    suspend fun existsByEmail(email: String): Boolean
}
package users.repository

import users.models.dto.AdminCreateDTO
import users.models.dto.AdminResponseDTO
import users.models.dto.AdminUpdateDTO
import java.util.*

interface AdminRepository {
    suspend fun createAdmin(admin: AdminCreateDTO): AdminResponseDTO
    suspend fun updateAdmin(adminId: UUID, admin: AdminUpdateDTO): AdminResponseDTO
    suspend fun deleteAdmin(adminId: UUID): Boolean
    suspend fun getAllAdmins(): List<AdminResponseDTO>
    suspend fun getAdminById(adminId: UUID): AdminResponseDTO?
    suspend fun getAdminByUserId(userId: UUID): AdminResponseDTO?
    suspend fun getAdminByEmail(email: String): AdminResponseDTO?
    suspend fun getAdminsByDepartment(department: String): List<AdminResponseDTO>
    suspend fun updateAdminAccessLevel(adminId: UUID, accessLevel: String): AdminResponseDTO
}

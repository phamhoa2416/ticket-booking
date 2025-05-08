package users.repository

import users.models.dto.CustomerCreateDTO
import users.models.dto.CustomerResponseDTO
import users.models.dto.CustomerUpdateDTO
import java.util.UUID

interface CustomerRepository {
    suspend fun createCustomer(customer: CustomerCreateDTO): CustomerResponseDTO
    suspend fun updateCustomer(customerId: UUID, customer: CustomerUpdateDTO): CustomerResponseDTO
    suspend fun deleteCustomer(customerId: UUID): Boolean
    suspend fun getAllCustomers(): List<CustomerResponseDTO>
    suspend fun getCustomerById(customerId: UUID): CustomerResponseDTO?
    suspend fun getCustomerByUserId(userId: UUID): CustomerResponseDTO?
    suspend fun getCustomerByEmail(email: String): CustomerResponseDTO?
    suspend fun getCustomerByPhone(phoneNumber: String): CustomerResponseDTO?
}
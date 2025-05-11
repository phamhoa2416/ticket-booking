package users.service

import users.models.dto.CustomerCreateDTO
import users.models.dto.CustomerResponseDTO
import users.models.dto.CustomerUpdateDTO
import users.models.types.UserRole
import users.repository.CustomerRepository
import users.repository.UserRepository
import java.util.*

private val logger = mu.KotlinLogging.logger {}

class CustomerService(
    private val userRepository: UserRepository,
    private val customerRepository: CustomerRepository
) {
    suspend fun createCustomer(customer: CustomerCreateDTO): CustomerResponseDTO {
        logger.info {"Creating new customer with user id: ${customer.userId}"}

        val user = userRepository.getUserById(customer.userId)
        if (user.role != UserRole.CUSTOMER) {
            logger.error { "User with ID ${customer.userId} is not a customer." }
            throw IllegalArgumentException("User must have CUSTOMER role.")
        }

        return try {
            customerRepository.createCustomer(customer).also {
                logger.info { "Customer created successfully with ID: ${it.id}" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to create customer: ${e.message}" }
            throw e
        }
    }

    suspend fun updateCustomer(customerId: UUID, customer: CustomerUpdateDTO): CustomerResponseDTO {
        logger.info { "Updating customer with ID: $customerId" }

        return try {
            customerRepository.updateCustomer(customerId, customer).also {
                logger.info { "Customer updated successfully with ID: ${it.id}" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to update customer: ${e.message}" }
            throw e
        }
    }

    suspend fun deleteCustomer(customerId: UUID): Boolean {
        logger.info { "Deleting customer with ID: $customerId" }

        return try {
            customerRepository.deleteCustomer(customerId).also {
                logger.info { "Customer deleted successfully with ID: $customerId" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to delete customer: ${e.message}" }
            throw e
        }
    }

    suspend fun getAllCustomers(): List<CustomerResponseDTO> {
        logger.info { "Fetching all customers" }

        return try {
            customerRepository.getAllCustomers().also {
                logger.info { "Fetched ${it.size} customers successfully" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch customers: ${e.message}" }
            throw e
        }
    }

    suspend fun getCustomerById(customerId: UUID): CustomerResponseDTO? {
        logger.info { "Fetching customer with ID: $customerId" }

        return try {
            customerRepository.getCustomerById(customerId).also {
                logger.info { "Fetched customer successfully with ID: $customerId" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch customer: ${e.message}" }
            throw e
        }
    }

    suspend fun getCustomerByUserId(userId: UUID): CustomerResponseDTO? {
        logger.info { "Fetching customer with user ID: $userId" }

        return try {
            customerRepository.getCustomerByUserId(userId).also {
                logger.info { "Fetched customer successfully with user ID: $userId" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch customer: ${e.message}" }
            throw e
        }
    }

    suspend fun getCustomerByEmail(email: String): CustomerResponseDTO? {
        logger.info { "Fetching customer with email: $email" }

        return try {
            customerRepository.getCustomerByEmail(email).also {
                logger.info { "Fetched customer successfully with email: $email" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch customer: ${e.message}" }
            throw e
        }
    }

    suspend fun getCustomerByPhone(phoneNumber: String): CustomerResponseDTO? {
        logger.info { "Fetching customer with phone number: $phoneNumber" }

        return try {
            customerRepository.getCustomerByPhone(phoneNumber).also {
                logger.info { "Fetched customer successfully with phone number: $phoneNumber" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch customer: ${e.message}" }
            throw e
        }
    }
}
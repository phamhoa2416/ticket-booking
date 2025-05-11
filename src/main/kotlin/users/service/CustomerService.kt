package users.service

import mu.KotlinLogging
import users.exceptions.InvalidUserRoleException
import users.exceptions.UserNotFoundException
import users.models.dto.CustomerCreateDTO
import users.models.dto.CustomerResponseDTO
import users.models.dto.CustomerUpdateDTO
import users.models.types.UserRole
import users.repository.CustomerRepository
import users.repository.UserRepository
import users.utility.UserUtils
import java.math.BigDecimal
import java.util.*

private val logger = KotlinLogging.logger {}

class CustomerService(
    private val customerRepository: CustomerRepository,
    private val userRepository: UserRepository
) {
    suspend fun createCustomer(customer: CustomerCreateDTO): CustomerResponseDTO {
        logger.info { "Creating new customer with id: ${customer.userId}" }

        val user = userRepository.getUserById(customer.userId) ?: throw UserNotFoundException(customer.userId)

        if (user.role != UserRole.CUSTOMER) {
            throw InvalidUserRoleException(customer.userId, UserRole.CUSTOMER)
        }

        return try {
            customerRepository.createCustomer(customer).also {
                logger.info { "Successfully created customer with ID: ${it.id}" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to create customer with id: ${customer.userId}" }
            throw e
        }
    }

    suspend fun updateCustomer(customerId: UUID, customer: CustomerUpdateDTO): CustomerResponseDTO {
        logger.info { "Updating customer with ID: $customerId" }
        return try {
            customerRepository.updateCustomer(customerId, customer).also {
                logger.info { "Successfully updated customer with ID: $customerId" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to update customer with ID: $customerId" }
            throw e
        }
    }

    suspend fun deleteCustomer(customerId: UUID) {
        logger.info { "Deleting customer with ID: $customerId" }
        try {
            if (!customerRepository.deleteCustomer(customerId)) {
                throw UserNotFoundException(customerId)
            }
            logger.info { "Successfully deleted customer with ID: $customerId" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to delete customer with ID: $customerId" }
            throw e
        }
    }

    suspend fun getCustomerById(id: UUID): CustomerResponseDTO {
        logger.info { "Fetching customer with ID: $id" }
        return try {
            customerRepository.getCustomerById(id)?.also {
                logger.info { "Successfully fetched customer with ID: $id" }
            } ?: throw NoSuchElementException("Customer not found")
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch customer with ID: $id" }
            throw e
        }
    }

    suspend fun getCustomerByUserId(userId: UUID): CustomerResponseDTO {
        logger.info { "Fetching customer for user ID: $userId" }
        return try {
            customerRepository.getCustomerByUserId(userId)?.also {
                logger.info { "Successfully fetched customer for user ID: $userId" }
            } ?: throw NoSuchElementException("Customer not found")
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch customer for user ID: $userId" }
            throw e
        }
    }

    suspend fun getAllCustomers(): List<CustomerResponseDTO> {
        logger.info { "Fetching all customers" }
        return try {
            customerRepository.getAllCustomers().also {
                logger.info { "Successfully fetched ${it.size} customers" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch customers" }
            throw e
        }
    }

    suspend fun updateCustomerPreferences(customerId: UUID, preferences: Map<String, Any>): CustomerResponseDTO {
        logger.info { "Updating preferences for customer ID: $customerId" }
        val updateDTO = CustomerUpdateDTO(
            preferredCategory = preferences["preferredCategory"] as? String,
            paymentMethods = (preferences["paymentMethods"] as? String)?.let { UserUtils.convertStringToPaymentMethods(it) }
        )
        return updateCustomer(customerId, updateDTO)
    }

    suspend fun updateCustomerSpending(customerId: UUID, amount: BigDecimal): CustomerResponseDTO {
        logger.info { "Updating spending for customer ID: $customerId" }
        val customer = getCustomerById(customerId)
        val updateDTO = CustomerUpdateDTO(
            totalSpending = customer.totalSpending + amount
        )
        return updateCustomer(customerId, updateDTO)
    }
}
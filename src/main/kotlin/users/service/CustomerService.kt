package users.service

import events.models.types.EventCategory
import mu.KotlinLogging
import users.exceptions.InvalidUserRoleException
import users.exceptions.UserNotFoundException
import users.models.dto.CustomerCreateDTO
import users.models.dto.CustomerResponseDTO
import users.models.dto.CustomerUpdateDTO
import users.models.types.UserRole
import users.repository.CustomerRepository
import users.repository.UserRepository
import users.utility.*
import java.math.BigDecimal
import java.util.*

private val logger = KotlinLogging.logger {}

class CustomerService(
    private val customerRepository: CustomerRepository,
    private val userRepository: UserRepository,
    private val cacheManager: CacheManager = CacheManager.getInstance()
) {
    suspend fun createCustomer(customer: CustomerCreateDTO): CustomerResponseDTO {
        logger.info { "Creating new customer with userId: ${customer.userId}" }

        val user = userRepository.getUserById(customer.userId)
        if (user.role != UserRole.CUSTOMER) {
            throw InvalidUserRoleException(customer.userId, UserRole.CUSTOMER)
        }

        ValidationUtils.apply {
            customer.preferredCategory?.let { validateCategory(EventCategory.valueOf(it)) }
            customer.paymentMethods?.forEach { validatePaymentMethod(it) }
            customer.totalSpending?.let { validateTotalSpending(it) }
            customer.loyaltyPoints?.let { validateLoyaltyPoints(it) }
        }

        return TransactionManager.withTransaction {
            try {
                val createdCustomer = customerRepository.createCustomer(customer)

                cacheManager.set("customer:${createdCustomer.id}", createdCustomer)

                AuditLogger.logUserAction(
                    userId = createdCustomer.user.id,
                    userRole = UserRole.CUSTOMER,
                    action = "CUSTOMER_CREATED",
                    details = mapOf(
                        "customerId" to createdCustomer.id,
                        "preferredCategory" to (createdCustomer.preferredCategory ?: "N/A"),
                        "loyaltyPoints" to createdCustomer.loyaltyPoints,
                    )
                )

                createdCustomer
            } catch (e: Exception) {
                AuditLogger.logError(
                    userId = customer.userId,
                    action = "CUSTOMER_CREATION_FAILED",
                    error = e,
                    details = mapOf(
                        "error" to (e.message ?: "Unknown error"),
                        "customerData" to customer
                    )
                )
                throw e
            }
        }
    }

    suspend fun updateCustomer(customerId: UUID, customer: CustomerUpdateDTO): CustomerResponseDTO {
        logger.info { "Updating customer with ID: $customerId" }

        ValidationUtils.apply {
            customer.preferredCategory?.let { validateCategory(EventCategory.valueOf(it)) }
            customer.paymentMethods?.forEach { validatePaymentMethod(it) }
            customer.totalSpending?.let { validateTotalSpending(it) }
            customer.loyaltyPoints?.let { validateLoyaltyPoints(it) }
        }

        return TransactionManager.withTransaction {
            try {
                val existingCustomer = customerRepository.getCustomerById(customerId)
                    ?: throw UserNotFoundException(customerId)

                val updatedCustomer = customerRepository.updateCustomer(customerId, customer)

                cacheManager.set("customer:${updatedCustomer.id}", updatedCustomer)

                AuditLogger.logUserAction(
                    userId = updatedCustomer.user.id,
                    userRole = UserRole.CUSTOMER,
                    action = "CUSTOMER_UPDATED",
                    details = mapOf(
                        "customerId" to updatedCustomer.id,
                        "preferredCategory" to (updatedCustomer.preferredCategory ?: "N/A"),
                        "loyaltyPoints" to updatedCustomer.loyaltyPoints,
                    )
                )

                updatedCustomer
            } catch (e: Exception) {
                AuditLogger.logError(
                    userId = null,
                    action = "CUSTOMER_UPDATE_FAILED",
                    error = e,
                    details = mapOf(
                        "error" to (e.message ?: "Unknown error"),
                        "customerData" to customer
                    )
                )
                throw e
            }
        }
    }

    suspend fun deleteCustomer(customerId: UUID): Boolean {
        logger.info { "Deleting customer with ID: $customerId" }

        return TransactionManager.withTransaction {
            try {
                val deleted = customerRepository.deleteCustomer(customerId)

                if (deleted) {
                    cacheManager.remove("customer:$customerId")

                    AuditLogger.logUserAction(
                        userId = customerId,
                        userRole = UserRole.CUSTOMER,
                        action = "CUSTOMER_DELETED",
                        details = mapOf(
                            "customerId" to customerId,
                            "loyaltyPoints" to 0,
                            "totalSpending" to BigDecimal.ZERO
                        )
                    )
                }

                deleted
            } catch (e: Exception) {
                AuditLogger.logError(
                    userId = null,
                    action = "CUSTOMER_DELETION_FAILED",
                    error = e,
                    details = mapOf("customerId" to customerId.toString())
                )
                throw e
            }
        }
    }

    suspend fun getCustomerById(id: UUID): CustomerResponseDTO {
        logger.info { "Fetching customer with ID: $id" }

        return cacheManager.getOrSet("customer:$id") {
            TransactionManager.withReadOnlyTransaction {
                customerRepository.getCustomerById(id)?.also { customer ->
                    AuditLogger.logDataAccess(
                        userId = customer.user.id,
                        userRole = UserRole.CUSTOMER,
                        resourceType = "Customer",
                        resourceId = id,
                        action = "GET_CUSTOMER"
                    )
                } ?: throw NoSuchElementException("Customer not found with ID: $id")
            }
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

    suspend fun updateLoyaltyPoints(customerId: UUID, points: Int): CustomerResponseDTO {
        logger.info { "Updating loyalty points for customer ID: $customerId" }

        ValidationUtils.validateLoyaltyPoints(points)

        return TransactionManager.withTransaction {
            try {
                val customer = customerRepository.getCustomerById(customerId)
                    ?: throw UserNotFoundException(customerId)

                val updatedCustomer = customerRepository.updateCustomer(
                    customerId,
                    CustomerUpdateDTO(loyaltyPoints = points)
                )

                cacheManager.set("customer:${updatedCustomer.id}", updatedCustomer)

                AuditLogger.logUserAction(
                    userId = updatedCustomer.user.id,
                    userRole = UserRole.CUSTOMER,
                    action = "LOYALTY_POINTS_UPDATED",
                    details = mapOf(
                        "customerId" to customerId,
                        "previousPoints" to customer.loyaltyPoints,
                        "newPoints" to points,
                        "pointsDifference" to (points - customer.loyaltyPoints)
                    )
                )

                updatedCustomer
            } catch (e: Exception) {
                AuditLogger.logError(
                    userId = null,
                    action = "LOYALTY_POINTS_UPDATE_FAILED",
                    error = e,
                    details = mapOf(
                        "customerId" to customerId,
                        "points" to points
                    )
                )
                throw e
            }
        }
    }

    suspend fun updateTotalSpending(customerId: UUID, amount: BigDecimal): CustomerResponseDTO {
        logger.info { "Updating total spending for customer ID: $customerId" }

        ValidationUtils.validateTotalSpending(amount)

        return TransactionManager.withTransaction {
            try {
                val customer = customerRepository.getCustomerById(customerId)
                    ?: throw UserNotFoundException(customerId)

                val updatedCustomer = customerRepository.updateCustomer(
                    customerId,
                    CustomerUpdateDTO(totalSpending = amount)
                )

                cacheManager.set("customer:${updatedCustomer.id}", updatedCustomer)

                AuditLogger.logPaymentEvent(
                    userId = updatedCustomer.user.id,
                    amount = amount,
                    currency = "USD",
                    status = "UPDATED",
                    details = mapOf(
                        "customerId" to customerId,
                        "previousSpending" to customer.totalSpending,
                        "newSpending" to amount,
                        "spendingDifference" to amount.subtract(customer.totalSpending)
                    )
                )

                updatedCustomer
            } catch (e: Exception) {
                AuditLogger.logError(
                    userId = null,
                    action = "TOTAL_SPENDING_UPDATE_FAILED",
                    error = e,
                    details = mapOf(
                        "customerId" to customerId,
                        "amount" to amount
                    )
                )
                throw e
            }
        }
    }
}
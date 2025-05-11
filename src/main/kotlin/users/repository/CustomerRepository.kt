package users.repository

import org.jetbrains.exposed.sql.transactions.transaction
import users.exceptions.UserNotFoundException
import users.models.dto.CustomerCreateDTO
import users.models.dto.CustomerResponseDTO
import users.models.dto.CustomerUpdateDTO
import users.models.entity.Customer
import users.models.entity.CustomerEntity
import users.models.entity.User
import users.models.entity.UserEntity
import users.utility.UserUtils
import java.math.BigDecimal
import java.util.*

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

class CustomerRepositoryImpl : CustomerRepository {
    override suspend fun createCustomer(customer: CustomerCreateDTO): CustomerResponseDTO = transaction {
        val user = UserEntity.findById(customer.userId) ?: throw UserNotFoundException(customer.userId)

        val customerEntity = CustomerEntity.new {
            this.user = user
            this.preferredCategory = customer.preferredCategory
            this.paymentMethods = customer.paymentMethods?.let { UserUtils.convertPaymentMethodsToString(it) }
            this.totalSpending = BigDecimal.ZERO
            this.loyaltyPoints = 0
        }

        UserUtils.toCustomerResponseDTO(customerEntity)
    }

    override suspend fun updateCustomer(customerId: UUID, customer: CustomerUpdateDTO): CustomerResponseDTO = transaction {
        val customerEntity = CustomerEntity.findById(customerId) ?: throw UserNotFoundException(customerId)

        customerEntity.apply {
            preferredCategory = customer.preferredCategory ?: preferredCategory
            paymentMethods = customer.paymentMethods?.let { UserUtils.convertPaymentMethodsToString(it) } ?: paymentMethods
            totalSpending = customer.totalSpending ?: totalSpending
            loyaltyPoints = customer.loyaltyPoints ?: loyaltyPoints
        }

        UserUtils.toCustomerResponseDTO(customerEntity)
    }

    override suspend fun deleteCustomer(customerId: UUID): Boolean = transaction {
        val customerEntity = CustomerEntity.findById(customerId) ?: return@transaction false
        customerEntity.delete()
        true
    }

    override suspend fun getAllCustomers(): List<CustomerResponseDTO> = transaction {
        CustomerEntity.all().map { UserUtils.toCustomerResponseDTO(it) }
    }

    override suspend fun getCustomerById(customerId: UUID): CustomerResponseDTO? = transaction {
        CustomerEntity.findById(customerId)?.let { UserUtils.toCustomerResponseDTO(it) }
    }

    override suspend fun getCustomerByUserId(userId: UUID): CustomerResponseDTO? = transaction {
        UserEntity.findById(userId)?.let { user ->
            CustomerEntity.find { Customer.customerId eq user.id }.firstOrNull()?.let { UserUtils.toCustomerResponseDTO(it) }
        }
    }

    override suspend fun getCustomerByEmail(email: String): CustomerResponseDTO? = transaction {
        UserEntity.find { User.email eq email }.firstOrNull()?.let { user ->
            CustomerEntity.find { Customer.customerId eq user.id }.firstOrNull()?.let { UserUtils.toCustomerResponseDTO(it) }
        }
    }

    override suspend fun getCustomerByPhone(phoneNumber: String): CustomerResponseDTO? = transaction {
        UserEntity.find { User.phoneNumber eq phoneNumber }.firstOrNull()?.let { user ->
            CustomerEntity.find { Customer.customerId eq user.id }.firstOrNull()?.let { UserUtils.toCustomerResponseDTO(it) }
        }
    }
}
package users.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import users.models.dto.CustomerCreateDTO
import users.models.dto.CustomerResponseDTO
import users.models.dto.CustomerUpdateDTO
import users.models.entity.Customer
import users.models.entity.CustomerEntity
import users.models.entity.User
import users.models.entity.UserEntity
import users.utils.UserUtility
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

class CustomerRepositoryImpl: CustomerRepository {
    override suspend fun createCustomer(customer: CustomerCreateDTO): CustomerResponseDTO = transaction {
        val user = UserEntity.findById(customer.userId) ?: throw NoSuchElementException("User not found")

        val customerEntity = CustomerEntity.new {
            this.user = user
            this.preferredCategory = customer.preferredCategory
            this.paymentMethods = customer.paymentMethods?.let { UserUtility.convertPaymentMethodsToString(it) }
            this.totalSpending = BigDecimal.ZERO
            this.loyaltyPoints = 0
        }

        UserUtility.toCustomerResponseDTO(customerEntity)
    }

    override suspend fun updateCustomer(customerId: UUID, customer: CustomerUpdateDTO): CustomerResponseDTO = transaction {
        val customerEntity = CustomerEntity.findById(customerId) ?: throw NoSuchElementException("Customer not found")

        customerEntity.preferredCategory?.let { customerEntity.preferredCategory = it }
        customerEntity.paymentMethods = customer.paymentMethods?.let {
            UserUtility.convertPaymentMethodsToString(it)
        }
        customerEntity.totalSpending = customer.totalSpending ?: customerEntity.totalSpending
        customerEntity.loyaltyPoints = customer.loyaltyPoints ?: customerEntity.loyaltyPoints

        UserUtility.toCustomerResponseDTO(customerEntity)
    }

    override suspend fun deleteCustomer(customerId: UUID): Boolean = transaction {
        val customerEntity = CustomerEntity.findById(customerId) ?: return@transaction false
        customerEntity.delete()
        true
    }

    override suspend fun getAllCustomers(): List<CustomerResponseDTO> = transaction {
        CustomerEntity.all().map { UserUtility.toCustomerResponseDTO(it) }
    }

    override suspend fun getCustomerById(customerId: UUID): CustomerResponseDTO? = transaction {
        CustomerEntity.findById(customerId)?.let { UserUtility.toCustomerResponseDTO(it) }
    }

    override suspend fun getCustomerByUserId(userId: UUID): CustomerResponseDTO? = transaction {
        CustomerEntity.find( Customer.customerId eq userId).firstOrNull()?.let { UserUtility.toCustomerResponseDTO(it) }
    }

    override suspend fun getCustomerByEmail(email: String): CustomerResponseDTO? = transaction {
        val user = UserEntity.find { User.email eq email }.firstOrNull() ?: return@transaction null
        CustomerEntity.find { Customer.customerId eq user.id }.firstOrNull()?.let { UserUtility.toCustomerResponseDTO(it) }
    }

    override suspend fun getCustomerByPhone(phoneNumber: String): CustomerResponseDTO? = transaction {
        val user = UserEntity.find { User.phoneNumber eq phoneNumber }.firstOrNull() ?: return@transaction null
        CustomerEntity.find { Customer.customerId eq user.id }.firstOrNull()?.let { UserUtility.toCustomerResponseDTO(it) }
    }
}
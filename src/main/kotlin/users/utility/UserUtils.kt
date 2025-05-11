package users.utility

import config.toKotlinxDateTime
import config.toKotlinxLocalDate
import mu.KotlinLogging
import users.models.dto.*
import users.models.entity.*
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

object UserUtils {
    fun validateUserCreateDTO(
        email: String,
        password: String,
        username: String,
        phoneNumber: String,
        dateOfBirth: LocalDate?
    ) {
        ValidationUtils.validateEmail(email)
        ValidationUtils.validatePassword(password)
        ValidationUtils.validateUsername(username)
        ValidationUtils.validatePhoneNumber(phoneNumber)
        ValidationUtils.validateDateOfBirth(dateOfBirth)
    }

    fun validateUserUpdateDTO(
        email: String?,
        username: String?,
        phoneNumber: String?,
        dateOfBirth: LocalDate?
    ) {
        email?.let {ValidationUtils.validateEmail(it) }
        username?.let { ValidationUtils.validateUsername(it) }
        phoneNumber?.let { ValidationUtils.validatePhoneNumber(it) }
        dateOfBirth?.let { ValidationUtils.validateDateOfBirth(it) }
    }


    fun UserEntity.toUserResponseDTO(): UserResponseDTO {
        return UserResponseDTO(
            id = id.value,
            email = email,
            username = username,
            phoneNumber = phoneNumber,
            dateOfBirth = dateOfBirth?.toKotlinxLocalDate(),
            avatarUrl = avatarUrl,
            role = role,
            isVerified = isVerified,
            createdAt = createdAt.toKotlinxDateTime(),
            updatedAt = updatedAt?.toKotlinxDateTime(),
            lastLogin = lastLogin?.toKotlinxDateTime(),
        )
    }

    fun toCustomerResponseDTO(customerEntity: CustomerEntity): CustomerResponseDTO {
        return CustomerResponseDTO(
            id = customerEntity.id.value,
            user = customerEntity.user.toUserResponseDTO(),
            totalSpending = customerEntity.totalSpending,
            loyaltyPoints = customerEntity.loyaltyPoints,
            preferredCategory = customerEntity.preferredCategory,
            paymentMethods = customerEntity.paymentMethods?.let { convertStringToPaymentMethods(it) },
            attendanceHistory = getAttendanceHistory()
        )
    }

    fun toOrganizeResponseDTO(organizerEntity: OrganizerEntity): OrganizerResponseDTO {
        return OrganizerResponseDTO(
            id = organizerEntity.id.value,
            user = organizerEntity.user.toUserResponseDTO(),
            organizationName = organizerEntity.organizationName,
            verificationStatus = organizerEntity.verificationStatus,
            contactEmail = organizerEntity.contactEmail,
            rating = organizerEntity.rating,
            totalEvents = organizerEntity.totalEvents
        )
    }

    fun AdminEntity.toAdminResponseDTO(): AdminResponseDTO {
        return AdminResponseDTO(
            id = id.value,
            user = user.toUserResponseDTO(),
            accessLevel = accessLevel,
            lastActivity = lastActivity?.toKotlinxDateTime(),
            department = department,
            actionHistory = getActionHistory()
        )
    }

    fun convertStringToPaymentMethods(paymentMethods: String): List<PaymentMethodDTO> {
        return emptyList() // TODO: Implement this method to convert the string to a list of PaymentMethodDTO
    }

    fun convertPaymentMethodsToString(paymentMethods: List<PaymentMethodDTO>): String {
        return "" // TODO: Implement this method to convert the list of PaymentMethodDTO to a string
    }

    fun getAttendanceHistory(): List<EventAttendanceDTO> {
        return emptyList() // TODO: Implement this method to fetch the attendance history
    }

    fun getActionHistory(): List<AdminActionDTO> {
        return emptyList() // TODO: Implement this method to fetch the action history
    }
}
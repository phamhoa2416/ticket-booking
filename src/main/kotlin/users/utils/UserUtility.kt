package users.utils

import config.toKotlinxDateTime
import config.toKotlinxLocalDate
import mu.KotlinLogging
import users.models.dto.*
import users.models.entity.AdminEntity
import users.models.entity.CustomerEntity
import users.models.entity.OrganizerEntity
import users.models.entity.UserEntity
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

object UserUtility {
    private const val MIN_PASSWORD_LENGTH = 8
    private const val MAX_PASSWORD_LENGTH = 100
    private const val MIN_USERNAME_LENGTH = 3
    private const val MAX_USERNAME_LENGTH = 50

    private val PASSWORD_PATTERN = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
    private val USERNAME_PATTERN = Regex("^[a-zA-Z0-9_-]{3,50}$")
    private val PHONE_PATTERN = Regex("^[+]?[0-9]{10,15}\$")
    private val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@(.+)$")

    fun validateUserCreateDTO(
        email: String,
        password: String,
        username: String,
        phoneNumber: String,
        dateOfBirth: LocalDate?
    ) {
        validateEmail(email)
        validatePassword(password)
        validateUsername(username)
        validatePhoneNumber(phoneNumber)
        validateDateOfBirth(dateOfBirth)
    }

    fun validateUserUpdateDTO(
        email: String?,
        username: String?,
        phoneNumber: String?,
        dateOfBirth: LocalDate?
    ) {
        email?.let { validateEmail(it) }
        username?.let { validateUsername(it) }
        phoneNumber?.let { validatePhoneNumber(it) }
        dateOfBirth?.let { validateDateOfBirth(it) }
    }

    private fun validateEmail(email: String) {
        require(email.matches(EMAIL_PATTERN)) { "Invalid email format" }
    }

    private fun validatePassword(password: String) {
        require(password.length in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH) {
            "Password must be between $MIN_PASSWORD_LENGTH and $MAX_PASSWORD_LENGTH characters"
        }
        require(password.matches(PASSWORD_PATTERN)) {
            "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character"
        }
    }

    private fun validateUsername(username: String) {
        require(username.length in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH) {
            "Username must be between $MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH characters"
        }
        require(username.matches(USERNAME_PATTERN)) {
            "Username can only contain letters, numbers, underscores, and hyphens"
        }
    }

    private fun validatePhoneNumber(phoneNumber: String?) {
        phoneNumber?.let {
            require(it.matches(PHONE_PATTERN)) { "Invalid phone number format" }
        }
    }

    private fun validateDateOfBirth(dateOfBirth: LocalDate?) {
        dateOfBirth?.let {
            require(it.isBefore(LocalDate.now())) { "Date of birth cannot be in the future" }
            require(it.isAfter(LocalDate.now().minusYears(150))) { "Invalid date of birth" }
        }
    }

    fun logValidationError(field: String, error: String) {
        logger.error { "Validation error for $field: $error" }
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
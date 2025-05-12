package users.utility

import config.toKotlinxDateTime
import config.toKotlinxLocalDate
import mu.KotlinLogging
import users.models.dto.*
import users.models.entity.*

private val logger = KotlinLogging.logger {}

object UserUtils {
    private fun UserEntity.toUserResponseDTO(): UserResponseDTO {
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

    private fun convertStringToPaymentMethods(paymentMethods: String): List<PaymentMethodDTO> {
        if (paymentMethods.isBlank()) return emptyList()
        
        return try {
            paymentMethods.split("|").map { method ->
                val parts = method.split(":")
                when (parts[0]) {
                    "CREDIT_CARD" -> PaymentMethodDTO.CreditCardDTO(
                        last4 = parts[1],
                        brand = parts[2],
                        expiration = parts[3]
                    )
                    "PAYPAL" -> PaymentMethodDTO.PayPalDTO(
                        email = parts[1]
                    )
                    else -> throw IllegalArgumentException("Unknown payment method type: ${parts[0]}")
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error converting payment methods string: $paymentMethods" }
            emptyList()
        }
    }

    fun convertPaymentMethodsToString(paymentMethods: List<PaymentMethodDTO>): String {
        if (paymentMethods.isEmpty()) return ""
        
        return paymentMethods.joinToString("|") { method ->
            when (method) {
                is PaymentMethodDTO.CreditCardDTO -> "CREDIT_CARD:${method.last4}:${method.brand}:${method.expiration}"
                is PaymentMethodDTO.PayPalDTO -> "PAYPAL:${method.email}"
            }
        }
    }

    private fun getAttendanceHistory(): List<EventAttendanceDTO> {
        // TODO: Implement this method to fetch attendance history
        return emptyList()
    }

    private fun getActionHistory(): List<AdminActionDTO> {
        // TODO: Implement this method to fetch action history
        return emptyList()
    }
}
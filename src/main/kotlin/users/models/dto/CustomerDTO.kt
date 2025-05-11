package users.models.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.UUID

@Serializable
data class CustomerCreateDTO(
    @Contextual val userId: UUID,
    val preferredCategory: String? = null,
    val paymentMethods: List<PaymentMethodDTO>? = null,
    @Contextual val totalSpending: BigDecimal? = null,
    val loyaltyPoints: Int? = null
)

@Serializable
data class CustomerUpdateDTO(
    val preferredCategory: String? = null,
    val paymentMethods: List<PaymentMethodDTO>? = null,
    @Contextual val totalSpending: BigDecimal? = null,
    val loyaltyPoints: Int? = null
)

@Serializable
sealed class PaymentMethodDTO {
    @Serializable
    @SerialName("credit_card")
    data class CreditCardDTO(
        val last4: String,
        val brand: String,
        val expiration: String,
        val isDefault: Boolean = false
    ) : PaymentMethodDTO()

    @Serializable
    @SerialName("paypal")
    data class PayPalDTO(
        val email: String,
        val isDefault: Boolean = false
    ) : PaymentMethodDTO()
}

@Serializable
data class CustomerResponseDTO(
    @Contextual val id: UUID,
    val user: UserResponseDTO,
    @Contextual val totalSpending: BigDecimal,
    val loyaltyPoints: Int = 0,
    val preferredCategory: String?,
    val paymentMethods: List<PaymentMethodDTO>?,
    val attendanceHistory: List<EventAttendanceDTO>? = null,
)

@Serializable
data class EventAttendanceDTO(
    @Contextual val eventId: UUID,
    val eventTitle: String,
    val attendedAt: LocalDateTime,
    val ticketCount: Int
)
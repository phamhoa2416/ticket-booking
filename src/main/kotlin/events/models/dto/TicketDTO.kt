package events.models.dto

import events.models.types.TicketStatus
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import users.models.dto.CustomerResponseDTO
import java.math.BigDecimal
import java.util.UUID

@Serializable
data class TicketCreateDTO(
    @Contextual val eventId: UUID,
    @Contextual val customerId: UUID,
    @Contextual val price: BigDecimal,
    val seatNumber: String? = null,
    val section: String? = null,
    val row: String? = null,
    val notes: String? = null
) {
    init {
        require(price >= BigDecimal.ZERO) { "Price cannot be negative" }
        require(seatNumber == null || seatNumber.matches(Regex("^[A-Z]\\d{1,3}$"))) {
            "Seat number must be in format: Letter followed by 1-3 digits (e.g., A1, B23)"
        }
        require(section == null || section.matches(Regex("^[A-Z][A-Z0-9]*$"))) {
            "Section must start with a letter and contain only letters and numbers"
        }
        require(row == null || row.matches(Regex("^[A-Z]\\d{1,2}$"))) {
            "Row must be in format: Letter followed by 1-2 digits (e.g., A1, B12)"
        }
    }
}

@Serializable
data class TicketUpdateDTO(
    val status: TicketStatus? = null,
    val seatNumber: String? = null,
    val section: String? = null,
    val row: String? = null,
    val notes: String? = null
) {
    init {
        seatNumber?.let {
            require(it.matches(Regex("^[A-Z]\\d{1,3}$"))) {
                "Seat number must be in format: Letter followed by 1-3 digits (e.g., A1, B23)"
            }
        }
        section?.let {
            require(it.matches(Regex("^[A-Z][A-Z0-9]*$"))) {
                "Section must start with a letter and contain only letters and numbers"
            }
        }
        row?.let {
            require(it.matches(Regex("^[A-Z]\\d{1,2}$"))) {
                "Row must be in format: Letter followed by 1-2 digits (e.g., A1, B12)"
            }
        }
    }
}

@Serializable
data class TicketTransferDTO(
    @Contextual val newCustomerId: UUID,
    val notes: String? = null
)

@Serializable
data class TicketRefundDTO(
    @Contextual val refundAmount: BigDecimal,
    val notes: String? = null
) {
    init {
        require(refundAmount >= BigDecimal.ZERO) { "Refund amount cannot be negative" }
    }
}

@Serializable
data class TicketResponseDTO(
    @Contextual val id: UUID,
    val ticketNumber: String,
    val event: EventResponseDTO,
    val customer: CustomerResponseDTO,
    @Contextual val price: BigDecimal,
    val status: TicketStatus,
    val purchaseDate: LocalDateTime,
    val validUntil: LocalDateTime,
    val seatNumber: String?,
    val section: String?,
    val row: String?,
    val transferCount: Int,
    val lastTransferDate: LocalDateTime?,
    @Contextual val refundAmount: BigDecimal?,
    val refundDate: LocalDateTime?,
    val notes: String?
) {
    val isActive: Boolean
        get() = TicketStatus.isActive(status)

    val canBeTransferred: Boolean
        get() = TicketStatus.canBeTransferred(status)

    val canBeRefunded: Boolean
        get() = TicketStatus.canBeRefunded(status)

    val isTerminal: Boolean
        get() = TicketStatus.isTerminated(status)

    val isExpired: Boolean
        get() = validUntil < Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val hasSeating: Boolean
        get() = !seatNumber.isNullOrBlank() || !section.isNullOrBlank() || !row.isNullOrBlank()
}

@Serializable
data class TicketSearchDTO(
    @Contextual val eventId: UUID? = null,
    @Contextual val customerId: UUID? = null,
    val status: TicketStatus? = null,
    val fromDate: LocalDateTime? = null,
    val toDate: LocalDateTime? = null,
    @Contextual val minPrice: BigDecimal? = null,
    @Contextual val maxPrice: BigDecimal? = null,
    val includeExpired: Boolean = false,
    val includeTerminal: Boolean = false
) {
    init {
        if (fromDate != null && toDate != null) {
            require(fromDate <= toDate) { "From date must be before or equal to to date" }
        }
        if (minPrice != null && maxPrice != null) {
            require(minPrice <= maxPrice) { "Min price must be less than or equal to max price" }
        }
    }
} 
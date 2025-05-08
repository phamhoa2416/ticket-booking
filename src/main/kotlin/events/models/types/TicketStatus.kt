package events.models.types

enum class TicketStatus {
    CONFIRMED,
    PENDING_PAYMENT,
    CANCELLED,
    REFUNDED,
    TRANSFERRED,
    EXPIRED,
    USED,
    INVALID;

    companion object {
        fun isActive(status: TicketStatus): Boolean {
            return status == CONFIRMED || status == TRANSFERRED
        }

        fun canBeTransferred(status: TicketStatus): Boolean {
            return status == CONFIRMED
        }

        fun canBeRefunded(status: TicketStatus): Boolean {
            return status in listOf(CONFIRMED, PENDING_PAYMENT)
        }

        fun isTerminated(status: TicketStatus): Boolean {
            return status in listOf(CANCELLED, REFUNDED, EXPIRED, USED, INVALID)
        }
    }
}

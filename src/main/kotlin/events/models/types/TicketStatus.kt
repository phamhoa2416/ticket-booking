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
            return status in listOf(CONFIRMED, TRANSFERRED)
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

        fun canBeCancelled(status: TicketStatus): Boolean {
            return status in listOf(CONFIRMED, PENDING_PAYMENT)
        }

        fun isPending(status: TicketStatus): Boolean {
            return status == PENDING_PAYMENT
        }

        fun isCompleted(status: TicketStatus): Boolean {
            return status == USED
        }

        fun isRefundable(status: TicketStatus): Boolean {
            return status in listOf(CONFIRMED, PENDING_PAYMENT)
        }
    }
}

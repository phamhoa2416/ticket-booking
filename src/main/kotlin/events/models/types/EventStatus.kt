package events.models.types

enum class EventStatus {
    DRAFT,
    PENDING_APPROVAL,
    PUBLISHED,
    ONGOING,
    COMPLETED,
    CANCELLED,
    POSTPONED,
    SOLD_OUT;

    companion object {
        fun isActive(status: EventStatus): Boolean {
            return status in listOf(PUBLISHED, ONGOING)
        }

        fun canBeModified(status: EventStatus): Boolean {
            return status in listOf(DRAFT, PENDING_APPROVAL)
        }

        fun canBeCancelled(status: EventStatus): Boolean {
            return status in listOf(PUBLISHED, ONGOING)
        }
    }
}


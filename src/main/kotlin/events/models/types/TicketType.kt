package events.models.types

enum class TicketType {
    GENERAL_ADMISSION,
    VIP,
    EARLY_BIRD,
    STUDENT,
    SENIOR,
    GROUP,
    FAMILY,
    DISABLED,
    LOYALTY,
    PRESALE,
    LAST_MINUTE,
    FLEXIBLE,
    UNKNOWN;

    companion object {
        fun fromString(value: String): TicketType {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                UNKNOWN
            }
        }
    }
}
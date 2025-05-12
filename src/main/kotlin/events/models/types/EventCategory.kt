package events.models.types

enum class EventCategory {
    CONCERT,
    CONFERENCE,
    SPORTS,
    THEATER,
    WORKSHOP,
    FESTIVAL,
    EXHIBITION,
    UNKNOWN,
    OTHER;

    companion object {
        fun fromString(value: String): EventCategory {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                OTHER
            }
        }
    }
}
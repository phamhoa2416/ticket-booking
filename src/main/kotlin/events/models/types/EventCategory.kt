package events.models.types

enum class EventCategory {
    CONCERT,
    CONFERENCE,
    SPORTS,
    THEATER,
    WORKSHOP,
    FESTIVAL,
    EXHIBITION,
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
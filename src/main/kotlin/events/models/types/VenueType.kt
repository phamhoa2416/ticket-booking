package events.models.types

enum class VenueType {
    INDOOR,
    OUTDOOR,
    VIRTUAL,
    STADIUM,
    THEATER,
    CONFERENCE_CENTER,
    CONVENTION_CENTER,
    ARENA,
    MUSEUM,
    GALLERY,
    RESTAURANT,
    HOTEL,
    UNIVERSITY,
    PARK,
    BEACH,
    OTHER;

    companion object {
        fun requiresSeating(type: VenueType): Boolean {
            return type in listOf(THEATER, CONFERENCE_CENTER, CONVENTION_CENTER, ARENA, STADIUM)
        }

        fun isVirtual(type: VenueType): Boolean {
            return type == VIRTUAL
        }

        fun isOutdoor(type: VenueType): Boolean {
            return type in listOf(OUTDOOR, STADIUM, PARK, BEACH)
        }
    }
}
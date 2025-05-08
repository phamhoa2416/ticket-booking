package events.models.dto

import events.models.types.EventCategory
import events.models.types.EventStatus
import events.models.types.VenueType
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import users.models.dto.OrganizerResponseDTO
import java.math.BigDecimal
import java.util.UUID

@Serializable
data class EventCreateDTO(
    val title: String,
    val description: String,
    val category: EventCategory,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val venueName: String,
    val venueType: VenueType,
    val address: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val capacity: Int,
    @Contextual val organizerId: UUID,
    @Contextual val basePrice: BigDecimal,
    val imageUrls: List<String> = emptyList(),
    val tags: List<String> = emptyList()
) {
    init {
        require(title.length in 3..255) { "Title must be between 3 and 255 characters" }
        require(description.length >= 10) { "Description must be at least 10 characters" }
        require(startDateTime < endDateTime) { "Start date must be before end date" }
        require(latitude in -90.0..90.0) { "Latitude must be in range -90 to 90" }
        require(longitude in -180.0..180.0) { "Longitude must be in range -180 to 180" }
        require(basePrice >= BigDecimal.ZERO) { "Base price must be non-negative" }

        // Venue type specific validations
        when (venueType) {
            VenueType.VIRTUAL -> {
                require(capacity == 0) { "Virtual venues should not have a capacity" }
                require(address.isBlank()) { "Virtual events should not have a physical address" }
            }
            in listOf(VenueType.THEATER, VenueType.CONFERENCE_CENTER, VenueType.CONVENTION_CENTER, VenueType.ARENA, VenueType.STADIUM) -> {
                require(capacity > 0) { "Seating-required venues must have a positive capacity" }
                require(capacity <= 50000) { "Seated venues should have capacity <= 50000" }
            }
            else -> {
                require(capacity <= 1000) { "Non-seated venues should have capacity <= 1000" }
            }
        }
    }
}

@Serializable
data class EventUpdateDTO(
    val title: String? = null,
    val description: String? = null,
    val category: EventCategory? = null,
    val status: EventStatus? = null,
    val startDateTime: LocalDateTime? = null,
    val endDateTime: LocalDateTime? = null,
    val venueName: String? = null,
    val venueType: VenueType? = null,
    val address: String? = null,
    val city: String? = null,
    val country: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val capacity: Int? = null,
    @Contextual val basePrice: BigDecimal? = null,
    val imageUrls: List<String>? = null,
    val tags: List<String>? = null
) {
    init {
        title?.let { require(it.length in 3..255) { "Title must be between 3-255 characters" } }
        description?.let { require(it.length >= 10) { "Description must be at least 10 characters" } }
        basePrice?.let { require(it >= BigDecimal.ZERO) { "Base price must be non-negative" } }
        latitude?.let { require(it in -90.0..90.0) { "Latitude must be in range -90 to 90" } }
        longitude?.let { require(it in -180.0..180.0) { "Longitude must be in range -180 to 180" } }
        
        if (startDateTime != null && endDateTime != null) {
            require(startDateTime < endDateTime) { "Start date must be before end date" }
        }

        // Venue type specific validations
        venueType?.let { type ->
            when (type) {
                VenueType.VIRTUAL -> {
                    capacity?.let { require(it == 0) { "Virtual venues should not have a capacity" } }
                    require(address.isNullOrBlank()) { "Virtual events should not have a physical address" }
                }
                in listOf(VenueType.THEATER, VenueType.CONFERENCE_CENTER, VenueType.CONVENTION_CENTER, VenueType.ARENA, VenueType.STADIUM) -> {
                    capacity?.let { 
                        require(it > 0) { "Seating-required venues must have a positive capacity" }
                        require(it <= 50000) { "Seated venues should have capacity <= 50000" }
                    }
                }
                else -> {
                    capacity?.let { require(it <= 1000) { "Non-seated venues should have capacity <= 1000" } }
                }
            }
        }
    }
}

@Serializable
data class EventResponseDTO(
    @Contextual val id: UUID,
    val title: String,
    val description: String,
    val category: EventCategory,
    val status: EventStatus,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val venueName: String,
    val venueType: VenueType,
    val address: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val capacity: Int,
    val availableTickets: Int,
    @Contextual val basePrice: BigDecimal,
    val organizer: OrganizerResponseDTO,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val imageUrls: List<String>?,
    val tags: List<String>?,
    val ticketStats: TicketStatsDTO? = null
) {
    val isActive: Boolean
        get() = EventStatus.isActive(status)

    val canBeModified: Boolean
        get() = EventStatus.canBeModified(status)

    val canBeCancelled: Boolean
        get() = EventStatus.canBeCancelled(status)

    val isVirtual: Boolean
        get() = VenueType.isVirtual(venueType)

    val isOutdoor: Boolean
        get() = VenueType.isOutdoor(venueType)

    val requiresSeating: Boolean
        get() = VenueType.requiresSeating(venueType)

    val isSoldOut: Boolean
        get() = availableTickets == 0

    val isUpcoming: Boolean
        get() = startDateTime > Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val isOngoing: Boolean
        get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) in startDateTime..endDateTime
}

@Serializable
data class TicketStatsDTO(
    val totalSold: Int,
    @Contextual val totalRevenue: BigDecimal,
    @Contextual val averagePrice: BigDecimal
)


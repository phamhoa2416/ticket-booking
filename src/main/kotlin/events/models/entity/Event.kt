package events.models.entity

import events.models.types.EventCategory
import events.models.types.EventStatus
import events.models.types.VenueType
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import users.models.entity.OrganizerEntity
import users.models.entity.Organizer
import java.math.BigDecimal
import java.util.UUID

object Event : UUIDTable() {
    val title = varchar("title", 255)
    val description = text("description")
    val category = enumerationByName("category", 50, EventCategory::class)
    val status = enumerationByName("status", 20, EventStatus::class).default(EventStatus.DRAFT)
    val startDateTime = datetime("start_date_time")
    val endDateTime = datetime("end_date_time")
    val venueName = varchar("venue_name", 255)
    val venueType = enumerationByName("venue_type", 20, VenueType::class)
    val address = text("address")
    val city = varchar("city", 100)
    val country = varchar("country", 100)
    val latitude = decimal("latitude", 9, 6)
    val longitude = decimal("longitude", 9, 6)
    val capacity = integer("capacity")
    val availableTickets = integer("available_tickets")
    val basePrice = decimal("base_price", 10, 2)
    val organizerId = reference("organizer_id", Organizer)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at").nullable()
    val imageUrls = text("image_urls").nullable()
    val tags = text("tags").nullable()
}

class EventEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EventEntity>(Event)

    var title by Event.title
    var description by Event.description
    var category by Event.category
    var status by Event.status
    var startDateTime by Event.startDateTime
    var endDateTime by Event.endDateTime
    var venueName by Event.venueName
    var venueType by Event.venueType
    var address by Event.address
    var city by Event.city
    var country by Event.country
    var latitude by Event.latitude
    var longitude by Event.longitude
    var capacity by Event.capacity
    var availableTickets by Event.availableTickets
    var basePrice by Event.basePrice
    var organizer by OrganizerEntity referencedOn Event.organizerId
    var createdAt by Event.createdAt
    var updatedAt by Event.updatedAt
    var imageUrls by Event.imageUrls
    var tags by Event.tags

    val tickets by TicketEntity referrersOn Ticket.eventId
}
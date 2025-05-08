package events.models.entity

import events.models.types.TicketStatus
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import users.models.entity.Customer
import users.models.entity.CustomerEntity
import java.util.UUID

object Ticket : UUIDTable() {
    val ticketNumber = varchar("ticket_number", 50).uniqueIndex()
    val eventId = reference("event_id", Event)
    val customerId = reference("customer_id", Customer)
    val price = decimal("price", 10, 2)
    val status = enumerationByName("status", 20, TicketStatus::class).default(TicketStatus.CONFIRMED)
    val purchaseDate = datetime("purchase_date")
    val validUntil = datetime("valid_until")
    val seatNumber = varchar("seat_number", 10).nullable()
    val section = varchar("section", 50).nullable()
    val row = varchar("row", 20).nullable()
    val transferCount = integer("transfer_count").default(0)
    val lastTransferDate = datetime("last_transfer_date").nullable()
    val refundAmount = decimal("refund_amount", 10, 2).nullable()
    val refundDate = datetime("refund_date").nullable()
    val notes = text("notes").nullable()
}

class TicketEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TicketEntity>(Ticket)

    var ticketNumber by Ticket.ticketNumber
    var eventId by EventEntity referencedOn Ticket.eventId
    var customer by CustomerEntity referencedOn Ticket.customerId
    var price by Ticket.price
    var status by Ticket.status
    var purchaseDate by Ticket.purchaseDate
    var validUntil by Ticket.validUntil
    var seatNumber by Ticket.seatNumber
    var section by Ticket.section
    var row by Ticket.row
    var transferCount by Ticket.transferCount
    var lastTransferDate by Ticket.lastTransferDate
    var refundAmount by Ticket.refundAmount
    var refundDate by Ticket.refundDate
    var notes by Ticket.notes
}
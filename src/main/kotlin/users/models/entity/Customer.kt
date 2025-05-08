package users.models.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.math.BigDecimal
import java.util.UUID

object Customer : UUIDTable() {
    val customerId = reference("customer_id", User).uniqueIndex()
    val totalSpending = decimal("total_spending", 10, 2).default(BigDecimal.ZERO)
    val preferredCategory = text("preferred_category").nullable()
    // predefined the Categories, and use enum to
    // store the preferred category with reference("category_id", Categories).nullable()
    val paymentMethods = text("payment_methods").nullable()
}

class CustomerEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CustomerEntity>(Customer)

    var user by UserEntity referencedOn Customer.customerId
    var totalSpending by Customer.totalSpending
    var preferredCategory by Customer.preferredCategory
    var paymentMethods by Customer.paymentMethods

    // Relationships
}
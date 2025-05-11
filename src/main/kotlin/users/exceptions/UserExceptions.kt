package users.exceptions

import users.models.types.UserRole
import users.models.types.VerificationStatus
import java.util.UUID

open class UserExceptions(
    val code: String,
    override val message: String,
    val details: Map<String, Any>? = null,
) : RuntimeException(message)

class UserNotFoundException(userId: UUID): UserExceptions(
    "USER_NOT_FOUND",
    "User with ID $userId not found",
    mapOf("userId" to userId),
)

class InvalidUserRoleException(userId: UUID, expectedRole: UserRole): UserExceptions(
    "INVALID_USER_ROLE",
    "User with ID $userId des not have the required role: $expectedRole",
    mapOf("userId" to userId, "expectedRole" to expectedRole),
)

class ConcurrentModificationException(message: String): UserExceptions(
    "CONCURRENT_MODIFICATION",
    message,
)

class SecurityException(message: String): UserExceptions(
    "SECURITY_EXCEPTION",
    message,
)

class ValidationException(field: String, message: String): UserExceptions(
    "VALIDATION_ERROR",
    "Validation failed for field '$field': $message",
    mapOf("field" to field, "message" to message)
)

class DuplicateResourceException(resourceType: String, identifier: String): UserExceptions(
    "DUPLICATE_RESOURCE",
    "$resourceType with identifier '$identifier' already exists",
    mapOf("resourceType" to resourceType, "identifier" to identifier)
)

class PaymentMethodException(message: String): UserExceptions(
    "PAYMENT_METHOD_ERROR",
    message
)

class OrganizerVerificationException(organizerId: UUID, status: VerificationStatus): UserExceptions(
    "ORGANIZER_VERIFICATION_ERROR",
    "Organizer with ID $organizerId has invalid verification status: $status",
    mapOf("organizerId" to organizerId, "status" to status)
)

class CustomerLoyaltyException(message: String): UserExceptions(
    "LOYALTY_ERROR",
    message
)

class DatabaseTransactionException(message: String): UserExceptions(
    "DATABASE_TRANSACTION_ERROR",
    message
)

class CacheException(message: String): UserExceptions(
    "CACHE_ERROR",
    message
)
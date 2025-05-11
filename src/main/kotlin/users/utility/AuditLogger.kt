package users.utility

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mu.KotlinLogging
import users.models.types.UserRole
import java.util.*

private val logger = KotlinLogging.logger {}

object AuditLogger {
    private const val AUDIT_LOG_PATTERN = "[AUDIT] %s | User: %s | Action: %s | Details: %s"

    fun logUserAction(
        userId: UUID,
        userRole: UserRole,
        action: String,
        details: Map<String, Any>
    ) {
        val timestamp = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val logMessage = String.format(
            AUDIT_LOG_PATTERN,
            timestamp,
            "User ID: $userId, Role: $userRole",
            action,
            details.toString()
        )
        logger.info { logMessage }
    }

    fun logSecurityEvent(
        userId: UUID,
        action: String,
        details: Map<String, Any>
    ) {
        val timestamp = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val userInfo = userId.toString()
        val logMessage = String.format(
            AUDIT_LOG_PATTERN,
            timestamp,
            userInfo,
            "SECURITY: $action",
            details.toString()
        )
        logger.warn { logMessage }
    }

    fun logDataAccess(
        userId: UUID?,
        userRole: UserRole?,
        resourceType: String,
        resourceId: UUID?,
        action: String,
    ) {
        val timestamp = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val details = mapOf(
            "resourceType" to resourceType,
            "resourceId" to resourceId.toString(),
            "action" to action
        )

        val logMessage = String.format(
            AUDIT_LOG_PATTERN,
            timestamp,
            "User ID: $userId, Role: $userRole",
            "DATA_ACCESS",
            details.toString()
        )
        logger.info { logMessage }
    }

    fun logPaymentEvent(
        userId: UUID,
        amount: java.math.BigDecimal,
        currency: String,
        status: String,
        details: Map<String, Any>
    ) {
        val timestamp = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val paymentDetails = details + mapOf(
            "amount" to amount,
            "currency" to currency,
            "status" to status
        )
        val logMessage = String.format(
            AUDIT_LOG_PATTERN,
            timestamp,
            userId,
            "PAYMENT",
            paymentDetails.toString()
        )
        logger.info { logMessage }
    }

    fun logSystemEvent(
        action: String,
        details: Map<String, Any>
    ) {
        val timestamp = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val logMessage = String.format(
            AUDIT_LOG_PATTERN,
            timestamp,
            "SYSTEM",
            action,
            details.toString()
        )
        logger.info { logMessage }
    }

    fun logError(
        userId: UUID?,
        action: String,
        error: Throwable,
        details: Map<String, Any>
    ) {
        val timestamp = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val userInfo = userId?.toString() ?: "SYSTEM"
        val errorDetails = details + mapOf(
            "errorType" to error.javaClass.simpleName,
            "errorMessage" to error.message
        )
        val logMessage = String.format(
            AUDIT_LOG_PATTERN,
            timestamp,
            userInfo,
            "ERROR: $action",
            errorDetails.toString()
        )
        logger.error { logMessage }
    }
}
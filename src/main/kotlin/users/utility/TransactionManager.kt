package users.utility

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import users.exceptions.DatabaseTransactionException

private val logger = mu.KotlinLogging.logger {}

object TransactionManager {
    suspend fun <T> withTransaction(block: suspend () -> T): T {
        return try {
            newSuspendedTransaction(Dispatchers.IO) { block() }
        } catch (e: Exception) {
            logger.error(e) { "Transaction failed" }
            throw DatabaseTransactionException("Failed to execute database transaction: ${e.message}")
        }
    }

    suspend fun <T> withReadOnlyTransaction(block: suspend () -> T): T {
        return try {
            newSuspendedTransaction(Dispatchers.IO) {
                connection.autoCommit = false
                connection.readOnly = true
                try {
                    block()
                } finally {
                    connection.autoCommit = true
                    connection.readOnly = false
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Read-only transaction failed" }
            throw DatabaseTransactionException("Failed to execute read-only database transaction: ${e.message}")
        }
    }

    suspend fun <T> withRetry(
        maxRetries: Int = 3,
        delayMillis: Long = 100,
        block: suspend () -> T
    ): T {
        var lastException: Exception? = null
        repeat(maxRetries) { attempt ->
            try {
                return withTransaction(block)
            } catch (e: Exception) {
                lastException = e
                logger.warn(e) { "Transaction failed on attempt ${attempt + 1}" }
                if (attempt < maxRetries - 1) {
                    kotlinx.coroutines.delay(delayMillis * (attempt + 1))
                }
            }
        }
        throw DatabaseTransactionException(
            "Failed to execute database transaction after $maxRetries attempts: ${lastException?.message}"
        )
    }
}
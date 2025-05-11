package users.utility

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import users.exceptions.CacheException
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

private val logger = mu.KotlinLogging.logger {}

class CacheManager {
    private val cache = ConcurrentHashMap<String, CacheEntry<Any>>()
    private val mutex = Mutex()
    private val expirationTime = 30.minutes

    data class CacheEntry<T>(
        val value: T,
        val expiration: Long
    )

    suspend fun <T> get(key: String): T? {
        return try {
            val entry = cache[key] ?: return null
            if (System.currentTimeMillis() > entry.expiration) {
                cache.remove(key)
                null
            } else {
                @Suppress("UNCHECKED_CAST")
                entry.value as T
            }
        } catch (e: Exception) {
            logger.error(e) {"Error retrieving value from cache for key: $key" }
            throw CacheException("Error retrieving value from cache for key: $key")
        }
    }

    suspend fun <T : Any> set(
        key: String,
        value: T,
        expiration: Duration = expirationTime
    ) {
        try {
            val expirationTime = System.currentTimeMillis() + expiration.inWholeMilliseconds
            cache[key] = CacheEntry(value, expirationTime)
        } catch (e: Exception) {
            logger.error(e) {"Error setting value in cache for key: $key" }
            throw CacheException("Error setting value in cache for key: $key")
        }
    }

    suspend fun remove(key: String) {
        try {
            cache.remove(key)
        } catch (e: Exception) {
            logger.error(e) {"Error removing value from cache for key: $key" }
            throw CacheException("Error removing value from cache for key: $key")
        }
    }

    suspend fun clear() {
        try {
            cache.clear()
        } catch (e: Exception) {
            logger.error(e) {"Error clearing cache" }
            throw CacheException("Failed to clear cache: ${e.message}")
        }
    }

    suspend fun <T : Any> getOrSet(
        key: String,
        expiration: Duration = expirationTime,
        loader: suspend () -> T
    ): T {
        return mutex.withLock {
            get<T>(key) ?: run {
                val value = loader()
                set(key, value, expiration)
                value
            }
        }
    }

    fun cleanup() {
        val now = System.currentTimeMillis()
        cache.entries.removeIf { entry ->
            entry.value.expiration <= now
        }
    }

    companion object {
        private val instance = CacheManager()

        fun getInstance(): CacheManager = instance
    }
}
package config

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.math.BigDecimal
import java.util.UUID

object SerializationModule {
    val module = SerializersModule {
        contextual(UUID::class, UUIDSerializer)
        contextual(BigDecimal::class, BigDecimalSerializer)
    }

    val json = Json {
        serializersModule = module
        encodeDefaults = true
        ignoreUnknownKeys = false
        prettyPrint = true
    }
}
package users.config

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.util.UUID

val uuidModule = SerializersModule {
    contextual(UUID::class, UUIDSerializer)
}

val json = Json {
    serializersModule = uuidModule
    encodeDefaults = true
    ignoreUnknownKeys = true
}
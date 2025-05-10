package plugins

import config.SerializationModule.json
import config.SerializationModule.module
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            serializersModule = module
            prettyPrint = true
            isLenient = true
        })
    }
}
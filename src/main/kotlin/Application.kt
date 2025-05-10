import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import plugins.*
import users.di.userModule

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(userModule)
    }
    configureDatabases()
    // configureSecurity()
    configureMonitoring()
    configureHTTP()
    configureRouting()
}

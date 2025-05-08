package users.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import users.models.dto.UserCreateDTO
import users.service.UserService

class UserController {
    private val userService: UserService by inject()

    fun Route.userRoutes() {
        route("/user") {
            post {
                try {
                    val userCreateDTO = call.receive<UserCreateDTO>()
                    val userResponse = userService.createUser(userCreateDTO)
                    call.respond(HttpStatusCode.Created, userResponse)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Bad Request")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Internal Server Error")
                }
            }
        }
    }
}
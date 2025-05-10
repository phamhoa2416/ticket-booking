package users.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import users.models.dto.UserCreateDTO
import users.models.dto.UserUpdateDTO
import users.service.UserService
import java.util.*

class UserController(private val userService: UserService) {

    fun Route.userRoutes() {
        route("/users") {
            // Create a new user
            post {
                try {
                    val userCreateDTO = call.receive<UserCreateDTO>()
                    val userResponse = userService.createUser(userCreateDTO)
                    call.respond(HttpStatusCode.Created, userResponse)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid input")))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create user"))
                }
            }

            // Get user by ID
            get("/{id}") {
                try {
                    val userId = UUID.fromString(call.parameters["id"] ?: throw IllegalArgumentException("Invalid user ID"))
                    val userResponse = userService.getUserById(userId)
                    call.respond(HttpStatusCode.OK, userResponse)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid input")))
                } catch (e: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to retrieve user"))
                }
            }

            // Update user
            put("/{id}") {
                try {
                    val userId = UUID.fromString(call.parameters["id"] ?: throw IllegalArgumentException("Invalid user ID"))
                    val userUpdateDTO = call.receive<UserUpdateDTO>()
                    val userResponse = userService.updateUser(userId, userUpdateDTO)
                    call.respond(HttpStatusCode.OK, userResponse)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid input")))
                } catch (e: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to update user"))
                }
            }

            // Delete user
            delete("/{id}") {
                try {
                    val userId = UUID.fromString(call.parameters["id"] ?: throw IllegalArgumentException("Invalid user ID"))
                    userService.deleteUser(userId)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid input")))
                } catch (e: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to delete user"))
                }
            }

            // Get user by username
            get ("/username/{username}") {
                try {
                    val username = call.parameters["username"] ?: throw IllegalArgumentException("Username is required")
                    val userResponse = userService.getUserByUsername(username)
                    if (userResponse != null) {
                        call.respond(HttpStatusCode.OK, userResponse)
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid input")))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to retrieve user"))
                }
            }

            // Get user by email
            get ("/email/{email}") {
                try {
                    val email = call.parameters["email"] ?: throw IllegalArgumentException("Email is required")
                    val userResponse = userService.getUserByEmail(email)
                    if (userResponse != null) {
                        call.respond(HttpStatusCode.OK, userResponse)
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid input")))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to retrieve user"))
                }
            }

            // Get all users
            get {
                try {
                    val page = call.parameters["page"]?.toIntOrNull() ?: 1
                    val pageSize = call.parameters["pageSize"]?.toIntOrNull() ?: 10

                    require(page > 0) { "Page number must be greater than 0" }
                    require(pageSize in 1..100) { "Page size must be between 1 and 100" }

                    val users = userService.findAll(page, pageSize)
                    call.respond(HttpStatusCode.OK, users)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid input")))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to retrieve users"))
                }
            }
        }
    }
}
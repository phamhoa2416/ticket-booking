package users.controllers

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import users.models.dto.CustomerCreateDTO
import users.models.dto.CustomerUpdateDTO
import users.service.CustomerService
import java.math.BigDecimal
import java.util.*

class CustomerController(private val customerService: CustomerService) {
    fun Route.customerRoutes() {
        route("/customers") {
            // Create a new customer
            post {
                try {
                    val customerCreateDTO = call.receive<CustomerCreateDTO>()
                    val customerResponse = customerService.createCustomer(customerCreateDTO)
                    call.respond(HttpStatusCode.Created, customerResponse)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid input")))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create customer"))
                }
            }

            // Get a customer by ID
            get("/{id}") {
                try {
                    val customerId = UUID.fromString(call.parameters["id"] ?: throw IllegalArgumentException("Invalid customer ID"))
                    val customerResponse = customerService.getCustomerById(customerId)
                    call.respond(HttpStatusCode.OK, customerResponse)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid input")))
                } catch (e: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Customer not found"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to retrieve customer"))
                }
            }

            // Update a customer
            put ("/{id}") {
                try {
                    val customerId = UUID.fromString(call.parameters["id"] ?: throw IllegalArgumentException("Invalid customer ID"))
                    val customerUpdateDTO = call.receive<CustomerUpdateDTO>()
                    val customerResponse = customerService.updateCustomer(customerId, customerUpdateDTO)
                    call.respond(HttpStatusCode.OK, customerResponse)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid input")))
                } catch (e: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Customer not found"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to update customer"))
                }
            }

            // Delete a customer
            delete("/{id}") {
                try {
                    val customerId = UUID.fromString(call.parameters["id"] ?: throw IllegalArgumentException("Invalid customer ID"))
                    customerService.deleteCustomer(customerId)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid input")))
                } catch (e: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Customer not found"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to delete customer"))
                }
            }

            // Get all customers
            get {
                try {
                    val customersResponse = customerService.getAllCustomers()
                    call.respond(HttpStatusCode.OK, customersResponse)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to retrieve customers"))
                }
            }

            // Update customer loyalty points
            put ("/id/loyalty-points") {
                try {
                    val customerId = UUID.fromString(call.parameters["id"] ?: throw IllegalArgumentException("Invalid customer ID"))
                    val preferences = call.receive<Int>()
                    val customerResponse = customerService.updateLoyaltyPoints(customerId, preferences)
                    call.respond(HttpStatusCode.OK, customerResponse)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid input")))
                } catch (e: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Customer not found"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to update customer preferences"))
                }
            }

            // Update customer spending
            put ("/id/spending") {
                try {
                    val customerId = UUID.fromString(call.parameters["id"] ?: throw IllegalArgumentException("Invalid customer ID"))
                    val amount = call.receive<Map<String, BigDecimal>>()["amount"] ?: throw IllegalArgumentException("Amount is required")
                    val customerResponse = customerService.updateTotalSpending(customerId, amount)
                    call.respond(HttpStatusCode.OK, customerResponse)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid input")))
                } catch (e: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Customer not found"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to update customer spending"))
                }
            }
        }
    }
}
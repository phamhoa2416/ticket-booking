package users.di

import users.controllers.CustomerController
import users.controllers.UserController
import users.repository.CustomerRepository
import users.repository.CustomerRepositoryImpl
import users.repository.UserRepository
import users.repository.UserRepositoryImpl
import users.service.CustomerService
import users.service.UserService

object UserDependencies {
    private val userRepository: UserRepository = UserRepositoryImpl()
    private val customerRepository: CustomerRepository = CustomerRepositoryImpl()


    private val userService: UserService = UserService(userRepository, customerRepository)
    val userController: UserController = UserController(userService)

    private val customerService: CustomerService = CustomerService(customerRepository, userRepository)
    val customerController: CustomerController = CustomerController(customerService)
}
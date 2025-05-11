package users.di

import users.controllers.UserController
import users.repository.UserRepository
import users.repository.UserRepositoryImpl
import users.service.UserService

object UserDependencies {
    private val userRepository: UserRepository = UserRepositoryImpl()
    private val userService: UserService = UserService(userRepository)
    val userController: UserController = UserController(userService)
}
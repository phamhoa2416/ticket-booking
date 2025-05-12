package users.di

import org.koin.dsl.module
import users.controllers.UserController
import users.repository.UserRepository
import users.repository.UserRepositoryImpl
import users.service.UserService

val userModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single { UserService(get(), get()) }
    single { UserController(get()) }
}
package pl.exbook.exbook.controllers

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.datamodel.User
import pl.exbook.exbook.exceptions.BadRequest
import pl.exbook.exbook.payload.request.LoginCredentials
import pl.exbook.exbook.repositories.UserRepository
import pl.exbook.exbook.services.UserService

@RestController
@RequestMapping("api/v1/auth")
class AuthController(private val userService: UserService) {

    @PostMapping("signup")
    fun signUp(@RequestBody user : User?) : User {
        if (user != null) {
            return userService.createUser(user)
        } else {
            throw BadRequest("Something is no yes")
        }
    }

    @PostMapping("login")
    fun signIn(@RequestBody loginCredentials: LoginCredentials) {

    }
}
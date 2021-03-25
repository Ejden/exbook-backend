package pl.exbook.exbook.security

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import pl.exbook.exbook.exceptions.BadRequest
import pl.exbook.exbook.user.DetailedUserDto
import pl.exbook.exbook.user.UserService

@RestController
@RequestMapping("api/v1/auth")
class AuthController(private val userService: UserService) {

    @PreAuthorize("permitAll()")
    @PostMapping("signup")
    fun signUp(@RequestBody request : CreateUserRequest?) : DetailedUserDto {
        if (request != null) {
            return userService.createUser(request).toDetailedUserDto()
        } else {
            throw BadRequest("Something is no yes")
        }
    }

    @PreAuthorize("permitAll()")
    @PostMapping("login")
    fun signIn(@RequestBody loginCredentials: LoginCredentials) {

    }
}

class CreateUserRequest (
    var login: String,
    var firstName: String,
    var lastName: String,
    var password: String,
    var email: String
) {
}

data class LoginCredentials(val login: String, val password: String) {

}


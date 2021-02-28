package pl.exbook.exbook.security

import org.springframework.data.annotation.Id
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.GrantedAuthority
import org.springframework.web.bind.annotation.*
import pl.exbook.exbook.exceptions.BadRequest
import pl.exbook.exbook.user.UserService
import java.time.Instant

@RestController
@RequestMapping("api/v1/auth")
class AuthController(private val userService: UserService) {

    @PreAuthorize("permitAll()")
    @PostMapping("signup")
    fun signUp(@RequestBody request : CreateUserRequest?) : UserDto {
        if (request != null) {
            return userService.createUser(request).toUserDto()
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
    var password: String,
    var email: String
) {
}

data class LoginCredentials(val login: String, val password: String) {

}

data class UserDto(
    @Id
    var id: String?,
    var login: String,
    var email: String,
    var phoneNumber: String?,
    var enabled : Boolean,
    var active: Boolean,
    var locked: Boolean,
    var credentialExpired: Boolean,
) {
    var authorities: MutableSet<GrantedAuthority> = mutableSetOf()
    var creationDate: Instant = Instant.now()
}
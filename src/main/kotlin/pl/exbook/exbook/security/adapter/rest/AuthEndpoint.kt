package pl.exbook.exbook.security.adapter.rest

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.GrantedAuthority
import org.springframework.web.bind.annotation.*
import pl.exbook.exbook.exceptions.BadRequest
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User
import java.time.Instant

@RestController
@RequestMapping("api/auth")
class AuthEndpoint(private val userFacade: UserFacade) {

    @PreAuthorize("permitAll()")
    @PostMapping("signup")
    fun signUp(@RequestBody request : CreateUserRequest?): DetailedUserDto {
        if (request != null) {
            return userFacade.createUser(request).toDetailedUserDto()
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
)

data class LoginCredentials(val login: String, val password: String)

data class DetailedUserDto (
    val id: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val phoneNumber: String?,
    val enabled : Boolean,
    val active: Boolean,
    val locked: Boolean,
    val credentialExpired: Boolean,
    val grade: Double,
    val authorities: MutableSet<GrantedAuthority>,
    val creationDate: Instant
)

private fun User.toDetailedUserDto() = DetailedUserDto(
    id = this.id!!.raw,
    firstName = this.firstName,
    lastName = this.lastName,
    username = this.login,
    email = this.email,
    phoneNumber = this.phoneNumber,
    enabled = this.enabled,
    active = this.active,
    locked = this.locked,
    credentialExpired = this.credentialExpired,
    grade = this.grade,
    creationDate = this.creationDate,
    authorities = this.authorities
)

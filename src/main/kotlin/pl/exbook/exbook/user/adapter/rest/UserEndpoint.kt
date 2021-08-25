package pl.exbook.exbook.user.adapter.rest

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User
import java.time.Instant

@RestController
@RequestMapping("api")
class UserController(private val userFacade: UserFacade) {

    @GetMapping("me")
    @PreAuthorize("isAuthenticated()")
    fun getCurrentUser(user: UsernamePasswordAuthenticationToken): DetailedUserDto? {
        return userFacade.getUserByUsername(user.name).toDetailedUserDto()
    }

    @GetMapping("users/{userId}")
    fun getUser(@PathVariable userId: UserId): UserDto {
        return userFacade.getUserById(userId).toUserDto()
    }
}

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

data class UserDto (
    val id: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val grade: Double,
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

private fun User.toUserDto() = UserDto(
    id = this.id!!.raw,
    firstName = this.firstName,
    lastName = this.lastName,
    username = this.login,
    grade = this.grade,
    creationDate = this.creationDate
)

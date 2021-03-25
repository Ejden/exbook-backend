package pl.exbook.exbook.user

import org.springframework.data.annotation.Id
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("api/v1/users")
@PreAuthorize("isAuthenticated()")
class UserController(
    private val userService: UserService
    ) {


    @GetMapping("me")
    fun getCurrentUser(user: UsernamePasswordAuthenticationToken): DetailedUserDto? {
        return userService.findUserByUsername(user.name)?.toUserDto()
    }

    @PostMapping
    fun createUser() {

    }
}

data class DetailedUserDto (
    @Id
    var id: String?,
    var firstName: String,
    var lastName: String,
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
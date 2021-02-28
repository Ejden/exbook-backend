package pl.exbook.exbook.user

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.security.UserDto

@RestController
@RequestMapping("api/v1/users")
@PreAuthorize("isAuthenticated()")
class UserController(
    private val userService: UserService
    ) {


    @GetMapping("me")
    fun getCurrentUser(user: UsernamePasswordAuthenticationToken): UserDto? {
        return userService.findUserByUsername(user.name)?.toUserDto()
    }

    @PostMapping
    fun createUser() {

    }
}
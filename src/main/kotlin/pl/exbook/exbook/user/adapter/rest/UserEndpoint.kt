package pl.exbook.exbook.user.adapter.rest

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.UserFacade
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import pl.exbook.exbook.shared.ContentType
import pl.exbook.exbook.user.adapter.rest.dto.CreateUserRequest
import pl.exbook.exbook.user.adapter.rest.dto.DetailedUserDto
import pl.exbook.exbook.user.adapter.rest.dto.UserDto
import pl.exbook.exbook.user.domain.User

@RestController
@RequestMapping("api")
class UserController(private val userFacade: UserFacade) {

    @GetMapping("me", produces = [ContentType.V1])
    @PreAuthorize("isAuthenticated()")
    fun getCurrentUser(user: UsernamePasswordAuthenticationToken): DetailedUserDto? {
        return userFacade.getUserByUsername(user.name).toDetailedUserDto()
    }

    @GetMapping("users/{userId}", produces = [ContentType.V1])
    fun getUser(@PathVariable userId: UserId): UserDto {
        return userFacade.getUserById(userId).toUserDto()
    }

    @PreAuthorize("permitAll()")
    @PostMapping("signup", consumes = [ContentType.V1], produces = [ContentType.V1])
    fun signUp(@RequestBody request : CreateUserRequest): DetailedUserDto {
        return userFacade.createUser(request.toCommand()).toDetailedUserDto()
    }

    @PostMapping("users/activation/{username}", produces = [ContentType.V1])
    fun activateAccount(@PathVariable username: String): DetailedUserDto {
        return userFacade.activateUserProfile(username).toDetailedUserDto()
    }

    @PostMapping("users/{username}/make-admin", produces = [ContentType.V1])
    fun makeAdmin(@PathVariable username: String): DetailedUserDto {
        return userFacade.addAdminAuthority(username).toDetailedUserDto()
    }
}

private fun User.toUserDto() = UserDto.fromDomain(this)
private fun User.toDetailedUserDto() = DetailedUserDto.fromDomain(this)

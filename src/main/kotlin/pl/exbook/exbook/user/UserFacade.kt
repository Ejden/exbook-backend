package pl.exbook.exbook.user

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.domain.CreateUserCommand
import pl.exbook.exbook.user.domain.Role
import pl.exbook.exbook.user.domain.User
import pl.exbook.exbook.user.domain.UserCreator
import pl.exbook.exbook.user.domain.UserNotFoundException
import pl.exbook.exbook.user.domain.UserRepository

@Service
class UserFacade(
    private val userRepository: UserRepository,
    private val userCreator: UserCreator
) {
    fun createUser(command: CreateUserCommand): User = userCreator.createUser(command)

    fun getUserByUsername(
        username: String
    ): User = userRepository.findByLogin(username) ?: throw UserNotFoundException(username)

    fun getUserById(
        userId: UserId
    ): User = userRepository.findById(userId) ?: throw UserNotFoundException(userId)

    fun activateUserProfile(
        username: String
    ): User {
        val user = userRepository.findByLogin(username) ?: throw UserNotFoundException(username)
        val activatedUser = user.activate()
        return userRepository.save(activatedUser)
    }

    fun addAdminAuthority(
        username: String
    ): User {
        val user = userRepository.findByLogin(username) ?: throw UserNotFoundException(username)
        user.authorities.add(SimpleGrantedAuthority(Role.ADMIN.value))
        return userRepository.save(user)
    }
}

package pl.exbook.exbook.user

import org.springframework.stereotype.Service
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.domain.CreateUserCommand
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
    ): User = userRepository.findByLogin(username) ?: throw UserNotFoundException("User with username $username not found")

    fun getUserById(
        userId: UserId
    ): User = userRepository.findById(userId) ?: throw UserNotFoundException("User with id $userId not found")
}

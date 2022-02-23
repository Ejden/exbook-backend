package pl.exbook.exbook.user

import com.mongodb.MongoWriteException
import java.util.UUID
import mu.KLogging
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pl.exbook.exbook.exceptions.UserAlreadyExistsException
import pl.exbook.exbook.security.adapter.rest.CreateUserRequest
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.domain.Authority
import pl.exbook.exbook.user.domain.Role
import pl.exbook.exbook.user.domain.User
import pl.exbook.exbook.user.domain.UserRepository

class UserFacade(
    private val userRepository: UserRepository,
) {

    companion object : KLogging()

    fun createUser(request: CreateUserRequest): User {
        try {
            val foundUser = userRepository.findByLoginOrEmail(request.username, request.email)

            if (foundUser == null) {
                val newUser = User(
                    id = UserId(UUID.randomUUID().toString()),
                    firstName = request.firstName,
                    lastName = request.lastName,
                    username = request.username,
                    password = BCryptPasswordEncoder().encode(request.password),
                    email = request.email,
                    phoneNumber = null,
                    enabled = true,
                    active = false,
                    locked = false,
                    credentialExpired = false,
                    grade = 0.0
                )

                newUser.authorities.add(SimpleGrantedAuthority(Role.USER.value))
                newUser.authorities.add(SimpleGrantedAuthority(Authority.SEARCH_BOOKS.value))
                newUser.authorities.add(SimpleGrantedAuthority(Authority.EXCHANGE_BOOKS.value))
                newUser.authorities.add(SimpleGrantedAuthority(Authority.READ_ABOUT_ME.value))

                val user = userRepository.insert(newUser)

                logger.info {"Created new user with id = ${newUser.id}" }

                return user
            } else {
                logger.error { "User with email ${request.email} or login ${request.username} already exists" }
                throw UserAlreadyExistsException();
            }
        } catch (e : MongoWriteException) {
            logger.error { "User with email ${request.email} or login ${request.username} already exists" }
            throw UserAlreadyExistsException()
        }
    }

    fun getUserByUsername(
        username: String
    ): User = userRepository.findByLogin(username) ?: throw UserNotFoundException("User with username $username not found")

    fun getUserById(
        userId: UserId
    ): User = userRepository.findById(userId) ?: throw UserNotFoundException("User with id $userId not found")
}

class UserNotFoundException(msg: String): RuntimeException(msg)

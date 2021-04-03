package pl.exbook.exbook.user

import com.mongodb.MongoWriteException
import mu.KotlinLogging
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.exbook.exbook.exceptions.UserAlreadyExistsException
import pl.exbook.exbook.security.CreateUserRequest

private val logger = KotlinLogging.logger {}

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    fun createUser(request: CreateUserRequest) : UserDatabaseModel {
        try {
            val foundUser = userRepository.findByLoginOrEmail(request.login, request.password)

            if (foundUser == null) {
                val newUser =  UserDatabaseModel(
                    id = null,
                    firstName = request.firstName,
                    lastName = request.lastName,
                    login = request.login,
                    password = BCryptPasswordEncoder().encode(request.password),
                    email = request.email,
                    phoneNumber = null,
                    enabled = true,
                    active = false,
                    locked = false,
                    credentialExpired = false,
                    grade = 0.0
                )

                newUser.authorities.add(SimpleGrantedAuthority(ROLE.USER.value))
                newUser.authorities.add(SimpleGrantedAuthority(AUTHORITY.SEARCH_BOOKS.value))
                newUser.authorities.add(SimpleGrantedAuthority(AUTHORITY.EXCHANGE_BOOKS.value))
                newUser.authorities.add(SimpleGrantedAuthority(AUTHORITY.READ_ABOUT_ME.value))

                val user = userRepository.insert(newUser)

                logger.info("Created new user with id = ${newUser.id}")

                return user
            } else {
                logger.error("User with email ${request.email} or login ${request.login} already exists")
                throw UserAlreadyExistsException();
            }
        } catch (e : MongoWriteException) {
            logger.error("User with email ${request.email} or login ${request.login} already exists")
            throw UserAlreadyExistsException()
        }
    }

    fun findUserByUsername(username: String) : User? {
        return userRepository.findByLogin(username)?.toUser()
    }

    fun findById(userId: String): User {
        return userRepository.findById(userId)
            .orElseThrow{ UserNotFoundException() }
            .toUser()
    }
}

class UserNotFoundException: RuntimeException()

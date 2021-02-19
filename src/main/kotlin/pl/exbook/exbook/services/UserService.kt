package pl.exbook.exbook.services

import com.mongodb.MongoWriteException
import mu.KotlinLogging
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import pl.exbook.exbook.controllers.UserDto
import pl.exbook.exbook.datamodel.AUTHORITY
import pl.exbook.exbook.datamodel.ROLE
import pl.exbook.exbook.datamodel.User
import pl.exbook.exbook.payload.request.CreateUserRequest
import pl.exbook.exbook.payload.request.UserAlreadyExistsException
import pl.exbook.exbook.repositories.UserRepository
import java.time.Instant

private val logger = KotlinLogging.logger {}

@Service
class UserService(private val userRepository: UserRepository) {

    fun createUser(request: CreateUserRequest) : UserDatabaseModel {
        try {
            val foundUser = userRepository.findByLoginOrEmail(request.login, request.password)

            if (foundUser == null) {
                val newUser =  UserDatabaseModel(
                    id = null,
                    login = request.login,
                    password = request.password,
                    email = request.email,
                    phoneNumber = null,
                    enabled = true,
                    active = false,
                    locked = false,
                    credentialExpired = false
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


}

@Document(collection = "users")
data class UserDatabaseModel(
    @Id
    var id: String?,
    var login: String,
    var password: String,
    var email: String,
    var phoneNumber: String?,
    var enabled : Boolean,
    var active: Boolean,
    var locked: Boolean,
    var credentialExpired: Boolean) {

    var authorities: MutableSet<GrantedAuthority> = mutableSetOf()
    var creationDate: Instant = Instant.now()

    fun toUser() : User {
        return User(id, login, password, email, phoneNumber, enabled, active, locked, credentialExpired)
    }

    fun toUserDto() : UserDto {
        return UserDto(id, login, email, phoneNumber, enabled, active, locked, credentialExpired)
    }
}
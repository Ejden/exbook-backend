package pl.exbook.exbook.user.domain

import com.mongodb.MongoWriteException
import java.util.UUID
import mu.KLogging
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.exbook.exbook.exceptions.UserAlreadyExistsException
import pl.exbook.exbook.shared.UserId

@Service
class UserCreator(private val userRepository: UserRepository) {
    fun createUser(command: CreateUserCommand): User {
        try {
            val foundUser = userRepository.findByLoginOrEmail(command.username, command.email)

            if (foundUser == null) {
                val newUser = User(
                    id = UserId(UUID.randomUUID().toString()),
                    firstName = command.firstName,
                    lastName = command.lastName,
                    username = command.username,
                    password = BCryptPasswordEncoder().encode(command.password),
                    email = command.email,
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
                logger.error { "User with email ${command.email} or login ${command.username} already exists" }
                throw UserAlreadyExistsException()
            }
        } catch (e: MongoWriteException) {
            logger.error { "User with email ${command.email} or login ${command.username} already exists" }
            throw UserAlreadyExistsException()
        }
    }

    companion object : KLogging()
}

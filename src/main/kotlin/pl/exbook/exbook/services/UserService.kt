package pl.exbook.exbook.services

import com.mongodb.MongoWriteException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import pl.exbook.exbook.datamodel.User
import pl.exbook.exbook.payload.request.UserAlreadyExistsException
import pl.exbook.exbook.repositories.UserRepository

private val logger = KotlinLogging.logger {}

@Service
class UserService(private val userRepository: UserRepository) {

    fun createUser(user: User) : User {
        try {
            return userRepository.save(user)
        } catch (e : MongoWriteException) {
            logger.error("User with email %s or login %s already exists", user.email, user.login)
            throw UserAlreadyExistsException()
        }
    }


}
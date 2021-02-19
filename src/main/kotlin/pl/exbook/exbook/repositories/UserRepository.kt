package pl.exbook.exbook.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import pl.exbook.exbook.services.UserDatabaseModel

interface UserRepository : MongoRepository<UserDatabaseModel, String> {
    fun findByLogin(login: String) : UserDatabaseModel?

    fun findByLoginOrEmail(login: String, email: String) : UserDatabaseModel?
}
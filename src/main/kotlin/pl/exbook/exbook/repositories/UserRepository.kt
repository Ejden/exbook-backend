package pl.exbook.exbook.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import pl.exbook.exbook.datamodel.User

interface UserRepository : MongoRepository<User, String> {
    fun findByLogin(login: String) : User?
}
package pl.exbook.exbook.user

import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<UserDatabaseModel, String> {
    fun findByLogin(login: String) : UserDatabaseModel?

    fun findByLoginOrEmail(login: String, email: String) : UserDatabaseModel?
}
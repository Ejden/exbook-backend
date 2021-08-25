package pl.exbook.exbook.user.adapter.mongodb

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.domain.User
import pl.exbook.exbook.user.domain.UserRepository

class DatabaseUserRepository(private val mongoUserRepository: MongoUserRepository) : UserRepository {

    override fun findById(userId: UserId): User? {
        val user = mongoUserRepository.findById(userId.raw)
        return if (user.isPresent) user.get().toDomain() else null
    }

    override fun findByLogin(login: String): User? {
       return mongoUserRepository.findByLogin(login)?.toDomain()
    }

    override fun findByLoginOrEmail(login: String, email: String): User? {
        return mongoUserRepository.findByLoginOrEmail(login, email)?.toDomain()
    }

    override fun insert(user: User): User {
        return mongoUserRepository.insert(user.toDocument()).toDomain()
    }
}

fun UserDocument.toDomain() = User(
    id = UserId(this.id!!),
    firstName = this.firstName,
    lastName = this.lastName,
    login = this.login,
    password = this.password,
    email = this.email,
    phoneNumber = this.phoneNumber,
    enabled = this.enabled,
    active = this.active,
    locked = this.locked,
    credentialExpired = this.credentialExpired,
    authorities = this.authorities,
    creationDate = this.creationDate,
    grade = this.grade
)

fun User.toDocument() = UserDocument(
    firstName = this.firstName,
    lastName = this.lastName,
    login = this.login,
    password = BCryptPasswordEncoder().encode(this.password),
    email = this.email,
    phoneNumber = this.phoneNumber,
    enabled = this.enabled,
    active = this.active,
    locked = this.locked,
    credentialExpired = this.credentialExpired,
    authorities = this.authorities,
    creationDate = this.creationDate,
    grade = this.grade
)

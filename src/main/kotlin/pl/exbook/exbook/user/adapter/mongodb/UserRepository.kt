package pl.exbook.exbook.user.adapter.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.security.core.GrantedAuthority
import pl.exbook.exbook.user.domain.User
import pl.exbook.exbook.user.domain.UserId
import java.time.Instant

interface UserRepository : MongoRepository<UserDocument, String> {
    fun findByLogin(login: String): UserDocument?

    fun findByLoginOrEmail(login: String, email: String): UserDocument?
}

@Document(collection = "users")
data class UserDocument(
    @Id
    val id: String? = null,
    val firstName: String,
    val lastName: String,
    val login: String,
    val password: String,
    val email: String,
    val phoneNumber: String?,
    val enabled : Boolean,
    val active: Boolean,
    val locked: Boolean,
    val credentialExpired: Boolean,
    val grade: Double,
    val authorities: MutableSet<GrantedAuthority> = mutableSetOf(),
    val creationDate: Instant = Instant.now()
)

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

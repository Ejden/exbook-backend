package pl.exbook.exbook.user.adapter.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.security.core.GrantedAuthority
import java.time.Instant

interface MongoUserRepository : MongoRepository<UserDocument, String> {

    fun findByUsername(login: String): UserDocument?

    fun findByUsernameOrEmail(login: String, email: String): UserDocument?
}

@Document(collection = "users")
data class UserDocument(
    @Id
    val id: String? = null,
    val firstName: String,
    val lastName: String,
    val username: String,
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

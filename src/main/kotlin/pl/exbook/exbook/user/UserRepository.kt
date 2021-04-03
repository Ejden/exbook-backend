package pl.exbook.exbook.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.security.core.GrantedAuthority
import java.time.Instant

interface UserRepository : MongoRepository<UserDatabaseModel, String> {
    fun findByLogin(login: String) : UserDatabaseModel?

    fun findByLoginOrEmail(login: String, email: String) : UserDatabaseModel?
}

@Document(collection = "users")
data class UserDatabaseModel(
    @Id
    var id: String?,
    var firstName: String,
    var lastName: String,
    var login: String,
    var password: String,
    var email: String,
    var phoneNumber: String?,
    var enabled : Boolean,
    var active: Boolean,
    var locked: Boolean,
    var credentialExpired: Boolean,
    var grade: Double) {

    var authorities: MutableSet<GrantedAuthority> = mutableSetOf()
    var creationDate: Instant = Instant.now()

    fun toUser() : User {
        return User(id, firstName, lastName, login, password, email, phoneNumber, enabled, active, locked, credentialExpired, authorities, creationDate, grade)
    }

    fun toDetailedUserDto() : DetailedUserDto {
        return DetailedUserDto(id, firstName, lastName, login, email, phoneNumber, enabled, active, locked, credentialExpired, grade)
    }
}
package pl.exbook.exbook.user.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pl.exbook.exbook.user.adapter.mongodb.UserDocument
import java.time.Instant

class User(
    val id: UserId,
    val firstName: String,
    val lastName: String,
    val login: String,
    val password: String,
    val email: String,
    val phoneNumber: String? = null,
    val enabled : Boolean = false,
    val active: Boolean = false,
    val locked: Boolean = false,
    val credentialExpired: Boolean = false,
    val authorities: MutableSet<GrantedAuthority> = mutableSetOf(),
    val creationDate: Instant = Instant.now(),
    val grade: Double = 0.0
) {
    fun toDocument() = UserDocument(
        id = this.id.raw,
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
}

enum class Authority(val value: String) {
    SEARCH_BOOKS("SEARCH_BOOKS"),
    EXCHANGE_BOOKS("EXCHANGE_BOOKS"),
    READ_ABOUT_ME("READ_ABOUT_ME")

}

enum class Role(val value: String) {
    USER("ROLE_USER"),
    MODERATOR("ROLE_MODERATOR"),
    ADMIN("ROLE_ADMIN")
}

data class UserId(val raw: String)

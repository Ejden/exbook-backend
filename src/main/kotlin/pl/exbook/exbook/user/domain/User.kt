package pl.exbook.exbook.user.domain

import org.springframework.security.core.GrantedAuthority
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
)

enum class AUTHORITY(val value: String) {
    SEARCH_BOOKS("SEARCH_BOOKS"),
    EXCHANGE_BOOKS("EXCHANGE_BOOKS"),
    READ_ABOUT_ME("READ_ABOUT_ME")

}

enum class ROLE(val value: String) {
    USER("ROLE_USER"),
    MODERATOR("ROLE_MODERATOR"),
    ADMIN("ROLE_ADMIN")
}

data class UserId(val raw: String)

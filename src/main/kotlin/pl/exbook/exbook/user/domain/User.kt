package pl.exbook.exbook.user.domain

import org.springframework.security.core.GrantedAuthority
import pl.exbook.exbook.shared.UserId
import java.time.Instant

data class User(
    val id: UserId,
    val firstName: String,
    val lastName: String,
    val username: String,
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
    fun activate() = this.copy(active = true)
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

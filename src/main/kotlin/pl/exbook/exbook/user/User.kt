package pl.exbook.exbook.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import pl.exbook.exbook.security.UserDto
import java.time.Instant

@Document(collection = "users")
class User () {
    @Id
    var id: String? = null
    var login: String? = null
    var password: String? = null
    var email: String? = null
    var phoneNumber: String? = null
    var enabled : Boolean = false
    var active: Boolean = false
    var locked: Boolean = false
    var credentialExpired: Boolean = false
    var authorities: MutableSet<GrantedAuthority> = mutableSetOf()
    var creationDate: Instant = Instant.now()

    constructor(id: String?) : this() {
        this.id = id
    }

    constructor(id: String?,
                login: String,
                password: String,
                email: String,
                phoneNumber: String?,
                enabled : Boolean,
                active: Boolean,
                locked: Boolean,
                credentialExpired: Boolean) : this() {
        this.id = id
        this.login = login
        this.password = password
        this.email = email
        this.phoneNumber = phoneNumber
        this.enabled = enabled
        this.active = active
        this.locked = locked
        this.credentialExpired = credentialExpired
    }

    constructor(id: String?,
                login: String,
                password: String,
                email: String,
                phoneNumber: String?,
                enabled: Boolean,
                active: Boolean,
                locked: Boolean,
                credentialExpired: Boolean,
                authorities: MutableSet<GrantedAuthority>,
                creationDate: Instant) : this(id, login, password, email, phoneNumber, enabled, active, locked, credentialExpired) {
        this.authorities = authorities
        this.creationDate = creationDate
    }

    fun toUserDto() : UserDto {
        return UserDto(id, login!!, email!!, phoneNumber, enabled, active, locked, credentialExpired)
    }
}

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
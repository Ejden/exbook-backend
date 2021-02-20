package pl.exbook.exbook.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import java.time.Instant

@Document(collection = "users")
class User (
    @Id
    var id: String?,
    var login: String,
    var password: String,
    var email: String,
    var phoneNumber: String?,
    var enabled : Boolean,
    var active: Boolean,
    var locked: Boolean,
    var credentialExpired: Boolean,
) {
    var authorities: MutableSet<GrantedAuthority> = mutableSetOf()
    var creationDate: Instant = Instant.now()

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
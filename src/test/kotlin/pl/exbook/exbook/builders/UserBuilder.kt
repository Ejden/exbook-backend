package pl.exbook.exbook.builders

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.domain.User
import java.time.Instant

class UserBuilder private constructor() {

    private var id: String = "user-id"
    private var firstName: String = "Jan"
    private var lastName: String = "Kowalski"
    private var login: String = "jkowalski"
    private var password: String = "secret-password"
    private var email: String = "jkowalski@gmail.com"
    private var phoneNumber: String = "123456789"
    private var enabled: Boolean = true
    private var active: Boolean = true
    private var locked: Boolean = false
    private var credentialExpired: Boolean = false
    private var authorities: MutableSet<GrantedAuthority> = mutableSetOf()
    private var creationDate: Instant = Instant.now()
    private var grade: Double = 0.0

    companion object {
        fun aUserBuilder(): UserBuilder {
            return UserBuilder()
        }
    }

    fun withLogin(login: String): UserBuilder {
        this.login = login
        return this
    }

    fun withPassword(password: String): UserBuilder {
        this.password = password
        return this
    }

    fun withAuthorities(authorities: MutableSet<GrantedAuthority>): UserBuilder {
        this.authorities = authorities
        return this
    }

    fun build(): User {
        return User(
                UserId(id),
                firstName,
                lastName,
                login,
                password,
                email,
                phoneNumber,
                enabled,
                active,
                locked,
                credentialExpired,
                authorities,
                creationDate,
                grade
        )
    }

    fun withAdminPrivileges(): UserBuilder {
        authorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))
        return this
    }

    fun withActiveAccount(): UserBuilder {
        this.enabled = true
        this.active = true
        this.locked = false
        this.credentialExpired = false
        return this
    }
}

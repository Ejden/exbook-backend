package pl.exbook.exbook.builders

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import pl.exbook.exbook.user.domain.User
import pl.exbook.exbook.user.domain.UserId

import java.time.Instant

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class UserBuilder {

    String id = "user-id"
    String firstName = "Jan"
    String lastName = "Kowalski"
    String login = "jkowalski"
    String password = "secret-password"
    String email = "jkowalski@gmail.com"
    String phoneNumber = "123456789"
    Boolean enabled = true
    Boolean active = true
    Boolean locked = false
    Boolean credentialExpired = false
    Set<GrantedAuthority> authorities = []
    Instant creationDate = Instant.now()
    Double grade = 0.0

    private UserBuilder() {}

    static UserBuilder aUserBuilder() {
        return new UserBuilder()
    }

    User build() {
        return new User(
                new UserId(id),
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

    UserBuilder withAdminPrivileges() {
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"))
        return this
    }

    UserBuilder withActiveAccount() {
        this.enabled = true
        this.active = true
        this.locked = false
        this.credentialExpired = false
        return this
    }
}

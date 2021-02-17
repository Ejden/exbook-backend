package pl.exbook.exbook.datamodel

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
    var phoneNumber: String,
    var enabled : Boolean,
    var active: Boolean,
    var locked: Boolean,
    var credentialExpired: Boolean,
) {
    var authorities: MutableSet<GrantedAuthority> = mutableSetOf()
    var creationDate: Instant = Instant.now()
}
package pl.exbook.exbook.user.adapter.rest.dto

import java.time.Instant
import org.springframework.security.core.GrantedAuthority
import pl.exbook.exbook.user.domain.User

data class DetailedUserDto (
    val id: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val phoneNumber: String?,
    val enabled : Boolean,
    val active: Boolean,
    val locked: Boolean,
    val credentialExpired: Boolean,
    val grade: Double,
    val creationDate: Instant
) {
    companion object {
        fun fromDomain(user: User) = DetailedUserDto(
            id = user.id.raw,
            firstName = user.firstName,
            lastName = user.lastName,
            username = user.username,
            email = user.email,
            phoneNumber = user.phoneNumber,
            enabled = user.enabled,
            active = user.active,
            locked = user.locked,
            credentialExpired = user.credentialExpired,
            grade = user.grade,
            creationDate = user.creationDate,
        )
    }
}

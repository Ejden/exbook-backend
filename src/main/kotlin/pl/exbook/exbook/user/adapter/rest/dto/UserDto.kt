package pl.exbook.exbook.user.adapter.rest.dto

import java.time.Instant
import pl.exbook.exbook.user.domain.User

data class UserDto (
    val id: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val grade: Double,
    val creationDate: Instant
) {
    companion object {
        fun fromDomain(user: User) = UserDto(
            id = user.id.raw,
            firstName = user.firstName,
            lastName = user.lastName,
            username = user.username,
            grade = user.grade,
            creationDate = user.creationDate
        )
    }
}

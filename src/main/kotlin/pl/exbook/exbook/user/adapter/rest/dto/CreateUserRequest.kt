package pl.exbook.exbook.user.adapter.rest.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size
import pl.exbook.exbook.user.domain.CreateUserCommand

data class CreateUserRequest(
    @field:Size(min = 5, max = 30)
    val username: String,
    @field:NotEmpty
    @field:Size(max = 256)
    val firstName: String,
    @field:Size(max = 256)
    @field:NotEmpty
    val lastName: String,
    @field:Size(min = 6, max = 256)
    val password: String,
    @field:Email
    val email: String
) {
    fun toCommand() = CreateUserCommand(
        username = username,
        firstName = firstName,
        lastName = lastName,
        password = password,
        email = email
    )
}

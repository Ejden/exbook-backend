package pl.exbook.exbook.user.domain

import pl.exbook.exbook.shared.IllegalParameterException

data class CreateUserCommand(
    val username: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val email: String
) {
    init {
        if (username.length < 5 || username.length > 30) {
            throw IllegalParameterException("username '$username' is too long or too short")
        }
        if (firstName.isEmpty() || firstName.length > 256) {
            throw IllegalParameterException("firstName '$firstName' is too long or too short")
        }
        if (lastName.isEmpty() || lastName.length > 256) {
            throw IllegalParameterException("lastName '$lastName' is too long or too short")
        }
        if (password.length < 6 || password.length > 256) {
            throw IllegalParameterException("password '$password' is too long or too short")
        }
        if (email.isEmpty()) {
            throw IllegalParameterException("Email is not correct")
        }
    }
}

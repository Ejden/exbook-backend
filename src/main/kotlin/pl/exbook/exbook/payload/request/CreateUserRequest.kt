package pl.exbook.exbook.payload.request

class CreateUserRequest (
    var login: String,
    var password: String,
    var email: String
        ) {
}
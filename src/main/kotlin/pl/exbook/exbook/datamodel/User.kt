package pl.exbook.exbook.datamodel

import org.springframework.data.annotation.Id

class User (
    @Id
    var id: String,
    var login: String,
    var password: String,
    var email: String,
    var phoneNumber: String,

        ) {
}
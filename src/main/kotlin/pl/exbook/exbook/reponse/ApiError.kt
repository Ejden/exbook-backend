package pl.exbook.exbook.reponse

import org.springframework.http.HttpStatus

class ApiError () {
    var httpStatus : HttpStatus? = null
    var message: String? = null
    var errors: MutableList<String> = mutableListOf()

    constructor(httpStatus: HttpStatus, message: String, error: String) : this() {
        this.httpStatus = httpStatus
        this.message = message
        this.errors.add(error)
    }

    constructor(httpStatus: HttpStatus, message: String, errors: MutableList<String>) : this() {
        this.httpStatus = httpStatus
        this.message = message
        this.errors = errors
    }
}
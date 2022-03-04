package pl.exbook.exbook.ability

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity
import pl.exbook.exbook.security.adapter.rest.LoginCredentials
import pl.exbook.exbook.shared.TestData.sampleAdminEmail
import pl.exbook.exbook.shared.TestData.sampleAdminUsername
import pl.exbook.exbook.shared.TestData.sampleSellerEmail
import pl.exbook.exbook.shared.TestData.sampleSellerPassword
import pl.exbook.exbook.shared.TestData.sampleSellerUsername
import pl.exbook.exbook.user.adapter.rest.dto.CreateUserRequest
import pl.exbook.exbook.user.adapter.rest.dto.DetailedUserDto
import pl.exbook.exbook.utils.createHttpEntity

class AuthAbility(private val restTemplate: TestRestTemplate) {
    private val regex = Regex("\\[Authorization=(.*); Max-Age=(.*); Expires=(.*); Path=(.*)]")

    fun getTokenForNewUser(
        username: String = sampleSellerUsername,
        firstName: String = "Tom",
        lastName: String = "Riddle",
        password: String = sampleSellerPassword,
        email: String = sampleSellerEmail
    ): String {
        thereIsAuthenticatedUser(username, firstName, lastName, password, email)

        val response = restTemplate.postForEntity(
            "/api/auth/login",
            createHttpEntity(LoginCredentials(username, password)),
            Any::class.java
        ).headers["Set-Cookie"].toString()

        return regex.matchEntire(response)!!.groups[1]!!.value
    }

    fun getTokenForNewAdmin(
        username: String = sampleAdminUsername,
        firstName: String = "Tom",
        lastName: String = "Riddle",
        password: String = sampleSellerPassword,
        email: String = sampleAdminEmail
    ): String {
        thereIsAdmin(username, firstName, lastName, password, email)

        val response = restTemplate.postForEntity(
            "/api/auth/login",
            createHttpEntity(LoginCredentials(username, password)),
            Any::class.java
        ).headers["Set-Cookie"].toString()

        return regex.matchEntire(response)!!.groups[1]!!.value
    }

    private fun thereIsAuthenticatedUser(
        username: String = sampleSellerUsername,
        firstName: String = "Tom",
        lastName: String = "Riddle",
        password: String = sampleSellerPassword,
        email: String = sampleSellerEmail
    ): ResponseEntity<DetailedUserDto> {
        val createUserRequest = CreateUserRequest(
            username = username,
            firstName = firstName,
            lastName = lastName,
            password = password,
            email = email
        )

        restTemplate.postForEntity(
            "/api/signup",
            createHttpEntity(body = createUserRequest, withAcceptHeader = true, withContentTypeHeader = true),
            DetailedUserDto::class.java
        )

        return restTemplate.postForEntity(
            "/api/users/activation/$username",
            createHttpEntity(body = null, withAcceptHeader = true),
            DetailedUserDto::class.java
        )
    }

    private fun thereIsAdmin(
        username: String = sampleSellerUsername,
        firstName: String = "Tom",
        lastName: String = "Riddle",
        password: String = sampleSellerPassword,
        email: String = sampleSellerEmail
    ): ResponseEntity<DetailedUserDto> {
        thereIsAuthenticatedUser(username, firstName, lastName, password, email)

        return restTemplate.postForEntity(
            "/api/users/$username/make-admin",
            createHttpEntity(null, withAcceptHeader = true),
            DetailedUserDto::class.java
        )
    }
}

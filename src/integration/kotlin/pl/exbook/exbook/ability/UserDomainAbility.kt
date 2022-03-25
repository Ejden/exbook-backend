package pl.exbook.exbook.ability

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import pl.exbook.exbook.shared.TestData.sampleSellerEmail
import pl.exbook.exbook.shared.TestData.sampleSellerPassword
import pl.exbook.exbook.shared.TestData.sampleSellerUsername
import pl.exbook.exbook.user.adapter.rest.dto.CreateUserRequest
import pl.exbook.exbook.user.adapter.rest.dto.DetailedUserDto
import pl.exbook.exbook.utils.createHttpEntity

class UserDomainAbility(private val restTemplate: TestRestTemplate) {
    fun thereIsUser(
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

    fun thereIsAdmin(
        username: String = sampleSellerUsername,
        firstName: String = "Tom",
        lastName: String = "Riddle",
        password: String = sampleSellerPassword,
        email: String = sampleSellerEmail
    ): ResponseEntity<DetailedUserDto> {
        thereIsUser(username, firstName, lastName, password, email)

        return restTemplate.postForEntity(
            "/api/users/$username/make-admin",
            createHttpEntity(null, withAcceptHeader = true),
            DetailedUserDto::class.java
        )
    }

    fun createUser(request: CreateUserRequest): ResponseEntity<String> {
        return restTemplate.postForEntity(
            "/api/signup",
            createHttpEntity(body = request, withAcceptHeader = true, withContentTypeHeader = true),
            String::class.java
        )
    }

    fun getUser(userId: String): ResponseEntity<String> {
        return restTemplate.getForEntity("/api/users/${userId}", String::class.java)
    }

    fun getCurrentLoggedUser(token: String? = null): ResponseEntity<String> {
        return restTemplate.exchange(
            "/api/me",
            HttpMethod.GET,
            createHttpEntity(body = null, withAcceptHeader = true, token = token),
            String::class.java
        )
    }

    fun getUserFromToken(token: String? = null): ResponseEntity<DetailedUserDto> {
        return restTemplate.exchange(
            "/api/me",
            HttpMethod.GET,
            createHttpEntity(body = null, withAcceptHeader = true, token = token),
            DetailedUserDto::class.java
        )
    }
}

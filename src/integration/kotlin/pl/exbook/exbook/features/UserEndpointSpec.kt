package pl.exbook.exbook.features

import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.json.shouldEqualSpecifiedJson
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import pl.exbook.exbook.BaseIntegrationSpec
import pl.exbook.exbook.ability.AuthAbility
import pl.exbook.exbook.ability.UserDomainAbility
import pl.exbook.exbook.shared.TestData.samplePassword
import pl.exbook.exbook.shared.TestData.sampleSellerEmail
import pl.exbook.exbook.shared.TestData.sampleSellerFirstName
import pl.exbook.exbook.shared.TestData.sampleSellerId
import pl.exbook.exbook.shared.TestData.sampleSellerLastName
import pl.exbook.exbook.shared.TestData.sampleSellerUsername
import pl.exbook.exbook.user.adapter.rest.dto.CreateUserRequest

class UserEndpointSpec(private val rest: TestRestTemplate) : BaseIntegrationSpec({
    val domain = UserDomainAbility(rest)
    val authDomain = AuthAbility(rest)

    should("create user") {
        // given
        val request = CreateUserRequest(
            username = sampleSellerUsername,
            firstName = "Tom",
            lastName = "Riddle",
            password = samplePassword,
            email = sampleSellerEmail
        )

        // when
        val response = domain.createUser(request)

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualSpecifiedJson """
            {
                "firstName": "Tom",
                "lastName": "Riddle",
                "username": "$sampleSellerUsername",
                "email": "$sampleSellerEmail",
                "phoneNumber": null,
                "enabled": true,
                "active": false,
                "locked": false,
                "credentialExpired": false,
                "grade": 0.0
            }
        """
        response.body!! shouldContainJsonKey "creationDate"
    }

    should("get user") {
        // given
        val user = domain.thereIsUser(
            username = sampleSellerUsername,
            firstName = sampleSellerFirstName,
            lastName = sampleSellerLastName,
            password = samplePassword,
            email = sampleSellerEmail
        )

        // when
        val response = domain.getUser(user.body!!.id)

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualJson  """
            {
                "id": "${user.body!!.id}",
                "firstName": "$sampleSellerFirstName",
                "lastName": "$sampleSellerLastName",
                "username": "$sampleSellerUsername",
                "grade": 0.0,
                "creationDate": "${user.body!!.creationDate}"
            }
        """
    }

    should("return status 404 when trying to get non existing user") {
        // when
        val response = domain.getUser(sampleSellerId.raw)

        // then
        response.statusCode shouldBe HttpStatus.NOT_FOUND
    }

    should("get current user") {
        // given
        val token = authDomain.getTokenForNewUser(
            username = sampleSellerUsername,
            firstName = sampleSellerFirstName,
            lastName = sampleSellerLastName,
            password = samplePassword,
            email = sampleSellerEmail
        )

        // when
        val response = domain.getCurrentLoggedUser(token)

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualSpecifiedJson   """
            {
                "firstName": "$sampleSellerFirstName",
                "lastName": "$sampleSellerLastName",
                "username": "$sampleSellerUsername",
                "grade": 0.0
            }
        """
    }

    should("return status 401 when trying to get current user without being logged in") {
        // when
        val response = domain.getCurrentLoggedUser()

        // then
        response.statusCode shouldBe HttpStatus.UNAUTHORIZED
    }
})

package pl.exbook.exbook.features.user

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.UserDomainAbility
import pl.exbook.exbook.shared.IllegalParameterException
import pl.exbook.exbook.user.domain.CreateUserCommand

class UserFacadeSpec : ShouldSpec({
    val domain = UserDomainAbility()

    should("create new user") {
        // given
        val command = CreateUserCommand(
            username = "jan.kowalski",
            firstName = "Jan",
            lastName = "Kowalski",
            password = "j.kowalski.pass",
            email = "j.kowalski@gmail.com"
        )

        // when
        val result = domain.facade.createUser(command)
        val createdUser = domain.facade.getUserById(result.id)

        // then
        createdUser.shouldNotBeNull()
        createdUser.firstName shouldBe "Jan"
        createdUser.lastName shouldBe "Kowalski"
        createdUser.username shouldBe "jan.kowalski"
        createdUser.email shouldBe "j.kowalski@gmail.com"
        createdUser.phoneNumber.shouldBeNull()
        createdUser.enabled shouldBe true
        createdUser.active shouldBe false
        createdUser.locked shouldBe false
        createdUser.credentialExpired shouldBe false
        createdUser.grade shouldBe 0.0
    }

    should("get existing user by id") {
        // given
        val createdUser = domain.facade.createUser(
            CreateUserCommand(
                username = "jan.kowalski",
                firstName = "Jan",
                lastName = "Kowalski",
                password = "j.kowalski.pass",
                email = "j.kowalski@gmail.com"
            )
        )

        // when
        val user = domain.facade.getUserById(createdUser.id)

        // then
        user.shouldNotBeNull()
        user.id shouldBe createdUser.id
    }

    should("get existing user by username") {
        // given
        domain.facade.createUser(
            CreateUserCommand(
                username = "jan.kowalski",
                firstName = "Jan",
                lastName = "Kowalski",
                password = "j.kowalski.pass",
                email = "j.kowalski@gmail.com"
            )
        )

        // when
        val user = domain.facade.getUserByUsername("jan.kowalski")

        // then
        user.shouldNotBeNull()
        user.username shouldBe "jan.kowalski"
    }

    context("should throw error while creating new user command") {
        withData(
            NewUserTestCase("shor", "Jan", "Kowalski", "j.kowalski.pass", "j.kowalski@gmail.com"),
            NewUserTestCase("1234567890123456789012345678901", "Jan", "Kowalski", "j.kowalski.pass", "j.kowalski@gmail.com"),
            NewUserTestCase("short", "", "Kowalski", "j.kowalski.pass", "j.kowalski@gmail.com"),
            NewUserTestCase("short", "Jan", "", "j.kowalski.pass", "j.kowalski@gmail.com"),
            NewUserTestCase("short", "Jan", "Kowalski", "short", "j.kowalski@gmail.com"),
            NewUserTestCase("short", "Jan", "Kowalski", "short", ""),
        ) { (username, firstName, lastName, password, email) ->
            // then
            shouldThrowExactly<IllegalParameterException> {
                CreateUserCommand(
                    username = username,
                    firstName = firstName,
                    lastName = lastName,
                    password = password,
                    email = email
                )
            }
        }
    }
})

internal data class NewUserTestCase(
    val username: String,
    val firstname: String,
    val lastname: String,
    val password: String,
    val email: String
)

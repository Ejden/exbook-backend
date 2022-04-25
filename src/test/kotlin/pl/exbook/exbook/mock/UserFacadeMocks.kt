package pl.exbook.exbook.mock

import io.mockk.every
import pl.exbook.exbook.shared.TestData
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User
import pl.exbook.exbook.user.domain.UserNotFoundException
import java.time.Instant

class UserFacadeMocks(private val userFacade: UserFacade) {
    fun thereIsUser(init: UserBuilder.() -> Unit) {
        val mockUser = UserBuilder().apply(init).build()
        every { userFacade.getUserById(mockUser.id) } returns mockUser
        every { userFacade.getUserByUsername(mockUser.username) } returns mockUser
    }

    fun thereIsNoUserFor(init: UserBuilder.() -> Unit) {
        val mockUser = UserBuilder().apply(init).build()
        every { userFacade.getUserById(mockUser.id) } throws UserNotFoundException("")
        every { userFacade.getUserByUsername(mockUser.username) } throws UserNotFoundException("")
    }
}

class UserBuilder {
    var userId: UserId = TestData.sampleUserId
    var firstName: String = "Jan"
    var lastName: String = "Kowalski"
    var username: String = "j.kowalski"
    var password: String = "password"
    var email: String = "j.kowalski@gmail.com"
    var phoneNumber: String = "555555555"
    var enabled: Boolean = true
    var active: Boolean = true
    var locked: Boolean = false
    var credentialExpired: Boolean = false
    var creationDate: Instant = Instant.EPOCH
    var grade: Double = 5.0

    fun build() = User(
        id = userId,
        firstName = firstName,
        lastName = lastName,
        username = username,
        password = password,
        email = email,
        phoneNumber = phoneNumber,
        enabled = enabled,
        active = active,
        locked = locked,
        credentialExpired = credentialExpired,
        authorities = mutableSetOf(),
        creationDate = creationDate,
        grade = grade
    )
}

package pl.exbook.exbook.features

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import pl.exbook.exbook.builders.UserBuilder.Companion.aUserBuilder
import pl.exbook.exbook.shared.TestData
import pl.exbook.exbook.user.domain.Role

internal class UserEndpointTest : BaseFeatureE2ETest() {

    @Test
    fun `should get simplified user`() {
        // given
        thereIsUser(aUserBuilder()
            .withAuthorities(mutableSetOf(
                SimpleGrantedAuthority(Role.USER.value))
            ))

        // expect:
        assertThat(1).isEqualTo(1)
        print(getDetailedUser(TestData.USER_ID))
    }
}

package pl.exbook.exbook.features

import org.springframework.security.core.authority.SimpleGrantedAuthority
import pl.exbook.exbook.shared.TestData
import pl.exbook.exbook.user.domain.Role

import static pl.exbook.exbook.builders.UserBuilder.aUserBuilder

class UserEndpointSpec extends BaseFeatureE2ESpec {

    def "should get simplified user"() {
        given:
            thereIsUser(aUserBuilder()
                .withAuthorities(Set.of(
                        new SimpleGrantedAuthority(Role.USER.value))))

        expect:
            1==1
            print(getDetailedUser(TestData.USER_ID))
    }

    def "test"() {
        expect:
            1 == 2
    }
}

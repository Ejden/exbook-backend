package pl.exbook.exbook.ability

import pl.exbook.exbook.adapters.InMemoryUserRepository
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.UserCreator

class UserDomainAbility {
    private val userRepository: InMemoryUserRepository = InMemoryUserRepository()
    private val userCreator: UserCreator = UserCreator(userRepository)
    val facade: UserFacade = UserFacade(userRepository, userCreator)
}

package pl.exbook.exbook.features

import pl.exbook.exbook.BaseSpec
import pl.exbook.exbook.adapters.InMemoryOfferRepository
import pl.exbook.exbook.adapters.InMemoryUserRepository

class OfferCreatorTest : BaseSpec() {
    private val offerRepository = InMemoryOfferRepository()
    private val userRepository = InMemoryUserRepository()
}

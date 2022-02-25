package pl.exbook.exbook.features.basket

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.shared.TestData.sampleUserId

class BasketFacadeSpec : ShouldSpec({
    val domain = BasketDomainAbility()

    should("create new basket when user tries to get one be it doesn't exist yet") {
        // given
        domain.thereIsUser(userId = sampleUserId)
        domain.thereIsNoBasketFor(userId = sampleUserId)

        // when
        val basket = domain.facade.getUserBasket(sampleUserId)

        // then
        basket.shouldNotBeNull()
        basket.userId shouldBe sampleUserId
        basket.itemsGroups.shouldBeEmpty()
    }
})

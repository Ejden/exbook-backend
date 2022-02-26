package pl.exbook.exbook.features.basket

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.BasketDomainAbility
import pl.exbook.exbook.basket.domain.AddItemToBasketCommand
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.TestData.sampleBuyerUsername
import pl.exbook.exbook.shared.TestData.sampleOfferId
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

    should("create basket while getting non existing basket for buyer id") {
        // given
        domain.thereIsUser(userId = sampleUserId, username = sampleBuyerUsername)
        domain.thereIsOffer(offerId = sampleOfferId)

        // when
        val basket = domain.facade.getUserBasket(sampleUserId)

        // then
        assertThat(basket).isNotNull()
        assertThat(basket.userId).isEqualTo(sampleUserId)
        assertThat(basket.itemsGroups).isEmpty()
    }

    should("create basket while getting non existing basket for username") {
        // given
        domain.thereIsUser(userId = sampleUserId, username = sampleBuyerUsername)
        domain.thereIsOffer(offerId = sampleOfferId)

        // when
        val basket = domain.facade.getUserBasket(sampleBuyerUsername)

        // then
        assertThat(basket).isNotNull()
        assertThat(basket.userId).isEqualTo(sampleUserId)
        assertThat(basket.itemsGroups).isEmpty()
    }

    should("get existing basket") {
        // given
        domain.thereIsUser(userId = sampleUserId, username = sampleBuyerUsername)

        // and: create basket by getting it
        domain.facade.getUserBasket(sampleBuyerUsername)

        // when
        val basket = domain.facade.getUserBasket(sampleBuyerUsername)

        // then
        assertThat(basket).isNotNull()
        assertThat(basket.userId).isEqualTo(sampleUserId)
        assertThat(basket.itemsGroups).isEmpty()
    }

    should("create new basket while adding new item to basket when it doesn't exist yet") {
        // given
        domain.thereIsUser(userId = sampleUserId, username = sampleBuyerUsername)
        domain.thereIsOffer(offerId = sampleOfferId)
        domain.thereIsNoBasketFor(userId = sampleUserId)

        val command = AddItemToBasketCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            quantity = 1,
            orderType = Order.OrderType.BUY
        )

        // when
        domain.facade.addItemToBasket(command)
        val basket = domain.facade.getUserBasket(sampleBuyerUsername)

        // then
        assertThat(basket).isNotNull()
        assertThat(basket.userId).isEqualTo(sampleUserId)
        assertThat(basket.itemsGroups).hasSize(1)
    }
})

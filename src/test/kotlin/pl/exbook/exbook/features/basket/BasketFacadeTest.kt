package pl.exbook.exbook.features.basket

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import org.junit.jupiter.api.Test
import pl.exbook.exbook.basket.domain.AddItemToBasketCommand
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.TestData.sampleBuyerUsername
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.shared.TestData.sampleUserId

class BasketFacadeTest : BasketFacadeTestSpecification() {
    @Test
    fun `should create new basket when user tries to get one be it doesn't exist yet`() {
        // given
        thereIsUser(userId = sampleUserId)
        thereIsNoBasketFor(userId = sampleUserId)

        // when
        val basket = basketFacade.getUserBasket(sampleUserId)

        // then
        assertThat(basket).isNotNull()
        assertThat(basket.userId).isEqualTo(sampleUserId)
        assertThat(basket.itemsGroups).isEmpty()
    }

    @Test
    fun `should get existing basket for buyer id`() {
        // given
        thereIsUser(userId = sampleUserId, username = sampleBuyerUsername)
        thereIsOffer(offerId = sampleOfferId)

        // when
        val basket = basketFacade.getUserBasket(sampleUserId)

        // then
        assertThat(basket).isNotNull()
        assertThat(basket.userId).isEqualTo(sampleUserId)
        assertThat(basket.itemsGroups).isEmpty()
    }

    @Test
    fun `should get existing basket for username`() {
        // given
        thereIsUser(userId = sampleUserId, username = sampleBuyerUsername)
        thereIsOffer(offerId = sampleOfferId)

        // when
        val basket = basketFacade.getUserBasket(sampleBuyerUsername)

        // then
        assertThat(basket).isNotNull()
        assertThat(basket.userId).isEqualTo(sampleUserId)
        assertThat(basket.itemsGroups).isEmpty()
    }

    @Test
    fun `should create new basket while adding new item to basket when it doesn't exist yet`() {
        // given
        thereIsUser(userId = sampleUserId, username = sampleBuyerUsername)
        thereIsOffer(offerId = sampleOfferId)
        thereIsNoBasketFor(userId = sampleUserId)

        val command = AddItemToBasketCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            quantity = 1,
            orderType = OrderType.BUY
        )

        // when
        basketFacade.addItemToBasket(command)
        val basket = basketFacade.getUserBasket(sampleBuyerUsername)

        // then
        assertThat(basket).isNotNull()
        assertThat(basket.userId).isEqualTo(sampleUserId)
        assertThat(basket.itemsGroups).hasSize(1)
    }
}

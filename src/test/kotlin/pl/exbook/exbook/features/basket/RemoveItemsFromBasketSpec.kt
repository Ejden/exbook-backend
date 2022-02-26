package pl.exbook.exbook.features.basket

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.BasketDomainAbility
import pl.exbook.exbook.basket.domain.AddItemToBasketCommand
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.TestData.otherSampleOfferId
import pl.exbook.exbook.shared.TestData.sampleBuyerId
import pl.exbook.exbook.shared.TestData.sampleBuyerUsername
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.shared.TestData.sampleSellerId
import pl.exbook.exbook.shared.TestData.sampleSellerUsername

class RemoveItemsFromBasketSpec : ShouldSpec({
    val domain = BasketDomainAbility()

    context("remove item from basket when offer is in two items groups with different order type and remove empty item group") {
        withData(
            Order.OrderType.BUY to Order.OrderType.EXCHANGE,
            Order.OrderType.EXCHANGE to Order.OrderType.BUY
        ) { (orderType, shouldLeftOrderType) ->
            // given
            domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
            domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
            domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
            listOf(Order.OrderType.BUY, Order.OrderType.EXCHANGE).forEach {
                domain.facade.addItemToBasket(
                    AddItemToBasketCommand(
                        username = sampleBuyerUsername,
                        offerId = sampleOfferId,
                        quantity = 2,
                        orderType = it
                    )
                )
            }

            // when
            domain.facade.removeItemFromBasket(sampleBuyerUsername, sampleOfferId, orderType)
            val basket = domain.facade.getUserBasket(sampleBuyerId)

            // then
            basket.itemsGroups shouldHaveSize 1

            // and
            val itemGroup = basket.itemsGroups.entries.toList()[0]
            itemGroup.key.orderType shouldBe shouldLeftOrderType
            itemGroup.key.sellerId shouldBe sampleSellerId

            itemGroup.value[0].offer.id shouldBe sampleOfferId
            itemGroup.value[0].quantity shouldBeExactly 2
        }
    }

    should("remove item from basket withing items group that have more than one item") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.thereIsOffer(offerId = otherSampleOfferId, sellerId = sampleSellerId)
        listOf(sampleOfferId, otherSampleOfferId).forEach {
            domain.facade.addItemToBasket(
                AddItemToBasketCommand(
                    username = sampleBuyerUsername,
                    offerId = it,
                    quantity = 2,
                    orderType = Order.OrderType.BUY
                )
            )
        }

        // when
        domain.facade.removeItemFromBasket(sampleBuyerUsername, sampleOfferId, Order.OrderType.BUY)
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then
        basket.itemsGroups shouldHaveSize 1

        // and
        val itemGroup = basket.itemsGroups.entries.toList()[0]
        itemGroup.key.orderType shouldBe Order.OrderType.BUY
        itemGroup.key.sellerId shouldBe sampleSellerId

        itemGroup.value[0].offer.id shouldBe otherSampleOfferId
        itemGroup.value[0].quantity shouldBeExactly 2
    }

    should("remove only item from basket") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 2,
                orderType = Order.OrderType.BUY
            )
        )

        // when
        domain.facade.removeItemFromBasket(sampleBuyerUsername, sampleOfferId, Order.OrderType.BUY)
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then
        basket.itemsGroups shouldHaveSize 0
    }

    should("not throw any error when trying to remove item from basket that is not in the basket") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 2,
                orderType = Order.OrderType.BUY
            )
        )

        // when
        domain.facade.removeItemFromBasket(sampleBuyerUsername, otherSampleOfferId, Order.OrderType.BUY)
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then
        basket.itemsGroups shouldHaveSize 1
    }
})

package pl.exbook.exbook.features.basket

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.BasketDomainAbility
import pl.exbook.exbook.basket.domain.AddItemToBasketCommand
import pl.exbook.exbook.basket.domain.BasketValidationException
import pl.exbook.exbook.basket.domain.ChangeItemQuantityCommand
import pl.exbook.exbook.offer.domain.OfferNotFoundException
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.TestData.otherSampleOfferId
import pl.exbook.exbook.shared.TestData.sampleBuyerId
import pl.exbook.exbook.shared.TestData.sampleBuyerUsername
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.shared.TestData.sampleSellerId
import pl.exbook.exbook.shared.TestData.sampleSellerUsername

class ChangeItemQuantityInBasketSpec : ShouldSpec({
    val domain = BasketDomainAbility()

    context("change item quantity in basket for offer in correct items group") {
        withData(
            TestCase(OrderType.BUY, 1, 2),
            TestCase(OrderType.EXCHANGE, 2, 1)
        ) { (orderType, exchangeOrderNewSize, buyOrderNewSize) ->
            // given
            domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
            domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
            domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
            domain.facade.addItemToBasket(
                AddItemToBasketCommand(
                    username = sampleBuyerUsername,
                    offerId = sampleOfferId,
                    quantity = 1,
                    orderType = OrderType.BUY
                )
            )
            domain.facade.addItemToBasket(
                AddItemToBasketCommand(
                    username = sampleBuyerUsername,
                    offerId = sampleOfferId,
                    quantity = 1,
                    orderType = OrderType.EXCHANGE
                )
            )

            val command = ChangeItemQuantityCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                orderType = orderType,
                newQuantity = 2
            )

            // when
            domain.facade.changeItemQuantityInBasket(command)
            val basket = domain.facade.getUserBasket(sampleBuyerId)

            // then
            basket.itemsGroups shouldHaveSize 2

            // and
            val firstGroup = basket.itemsGroups.entries.toList()[0]

            firstGroup.key.orderType shouldBe OrderType.BUY
            firstGroup.value shouldHaveSize 1
            firstGroup.value[0].offer.id shouldBe sampleOfferId
            firstGroup.value[0].quantity shouldBe buyOrderNewSize

            // and
            val secondGroup = basket.itemsGroups.entries.toList()[1]

            secondGroup.key.orderType shouldBe OrderType.EXCHANGE
            secondGroup.value shouldHaveSize 1
            secondGroup.value[0].offer.id shouldBe sampleOfferId
            secondGroup.value[0].quantity shouldBe exchangeOrderNewSize
        }
    }

    context("change item quantity not changing items order in items group") {
        withData(
            sampleOfferId,
            otherSampleOfferId
        ) { offerId ->
            // given
            domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
            domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
            domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
            domain.thereIsOffer(offerId = otherSampleOfferId, sellerId = sampleSellerId)
            domain.facade.addItemToBasket(
                AddItemToBasketCommand(
                    username = sampleBuyerUsername,
                    offerId = sampleOfferId,
                    quantity = 2,
                    orderType = OrderType.BUY
                )
            )
            domain.facade.addItemToBasket(
                AddItemToBasketCommand(
                    username = sampleBuyerUsername,
                    offerId = otherSampleOfferId,
                    quantity = 3,
                    orderType = OrderType.BUY
                )
            )

            val command = ChangeItemQuantityCommand(
                username = sampleBuyerUsername,
                offerId = offerId,
                orderType = OrderType.BUY,
                newQuantity = 5
            )

            // when
            var basket = domain.facade.getUserBasket(sampleBuyerId)

            // then
            basket.itemsGroups shouldHaveSize 1

            var itemGroup = basket.itemsGroups.entries.toList()[0]
            itemGroup.value shouldHaveSize 2
            itemGroup.value[0].offer.id shouldBe sampleOfferId
            itemGroup.value[1].offer.id shouldBe otherSampleOfferId

            // when
            domain.facade.changeItemQuantityInBasket(command)
            basket = domain.facade.getUserBasket(sampleBuyerId)

            // then
            basket.itemsGroups shouldHaveSize 1

            itemGroup = basket.itemsGroups.entries.toList()[0]
            itemGroup.value shouldHaveSize 2
            itemGroup.value[0].offer.id shouldBe sampleOfferId
            itemGroup.value[1].offer.id shouldBe otherSampleOfferId
        }
    }

    should("remove item if new quantity to zero") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.thereIsOffer(offerId = otherSampleOfferId, sellerId = sampleSellerId)
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = OrderType.BUY
            )
        )
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = otherSampleOfferId,
                quantity = 1,
                orderType = OrderType.BUY
            )
        )

        val command = ChangeItemQuantityCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            orderType = OrderType.BUY,
            newQuantity = 0
        )

        // when
        domain.facade.changeItemQuantityInBasket(command)
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then
        basket.itemsGroups shouldHaveSize 1

        // and
        val itemGroup = basket.itemsGroups.entries.toList()[0]
        itemGroup.value shouldHaveSize 1
        itemGroup.value[0].offer.id shouldBe otherSampleOfferId
        itemGroup.value[0].quantity shouldBe 1
    }

    should("remove whole items group if new quantity is zero and it's the only item in group") {
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = OrderType.BUY
            )
        )
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = OrderType.EXCHANGE
            )
        )

        val command = ChangeItemQuantityCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            orderType = OrderType.BUY,
            newQuantity = 0
        )

        // when
        domain.facade.changeItemQuantityInBasket(command)
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then
        basket.itemsGroups shouldHaveSize 1

        // and
        val itemGroup = basket.itemsGroups.entries.toList()[0]

        itemGroup.key.orderType shouldBe OrderType.EXCHANGE
        itemGroup.value shouldHaveSize 1
        itemGroup.value[0].offer.id shouldBe sampleOfferId
        itemGroup.value[0].quantity shouldBe 1
    }

    should("create new item group with new item when changing item quantity and item is not in the basket") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)

        val command = ChangeItemQuantityCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            orderType = OrderType.BUY,
            newQuantity = 10
        )

        // when
        domain.facade.changeItemQuantityInBasket(command)
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then
        basket.itemsGroups shouldHaveSize 1

        // and
        val itemGroup = basket.itemsGroups.entries.toList()[0]

        itemGroup.key.sellerId shouldBe sampleSellerId
        itemGroup.key.orderType shouldBe OrderType.BUY
        itemGroup.value shouldHaveSize 1
        itemGroup.value[0].offer.id shouldBe sampleOfferId
        itemGroup.value[0].quantity shouldBe 10
    }

    should("add to existing item group new item when changing item quantity that is not in the basket yet") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.thereIsOffer(offerId = otherSampleOfferId, sellerId = sampleSellerId)
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 5,
                orderType = OrderType.BUY
            )
        )

        val command = ChangeItemQuantityCommand(
            username = sampleBuyerUsername,
            offerId = otherSampleOfferId,
            orderType = OrderType.BUY,
            newQuantity = 10
        )

        // when
        domain.facade.changeItemQuantityInBasket(command)
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then
        basket.itemsGroups shouldHaveSize 1

        // and
        val itemGroup = basket.itemsGroups.entries.toList()[0]

        itemGroup.key.sellerId shouldBe sampleSellerId
        itemGroup.key.orderType shouldBe OrderType.BUY
        itemGroup.value shouldHaveSize 2
        itemGroup.value[0].offer.id shouldBe sampleOfferId
        itemGroup.value[0].quantity shouldBe 5
        itemGroup.value[1].offer.id shouldBe otherSampleOfferId
        itemGroup.value[1].quantity shouldBe 10
    }

    context("throw error when trying to change quantity to incorrect value") {
        withData(-2L, -1L) { newQuantity ->
            // given
            domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
            domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
            domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
            domain.facade.addItemToBasket(
                AddItemToBasketCommand(
                    username = sampleBuyerUsername,
                    offerId = sampleOfferId,
                    quantity = 5,
                    orderType = OrderType.BUY
                )
            )

            val command = ChangeItemQuantityCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                orderType = OrderType.BUY,
                newQuantity = newQuantity
            )

            // then
            shouldThrowExactly<BasketValidationException> {
                domain.facade.changeItemQuantityInBasket(command)
            }
        }
    }

    should("throw error when trying to change item quantity on non existing offer") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsNoOfferFor(offerId = sampleOfferId)

        val command = ChangeItemQuantityCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            orderType = OrderType.BUY,
            newQuantity = 2
        )

        // then
        shouldThrowExactly<OfferNotFoundException> {
            domain.facade.changeItemQuantityInBasket(command)
        }
    }
})

private data class TestCase(
    val updatingOrder: OrderType,
    val exchangeOrderNewSize: Long,
    val buyOrderNewSize: Long
)

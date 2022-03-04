package pl.exbook.exbook.features.basket

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.BasketDomainAbility
import pl.exbook.exbook.basket.domain.AddItemToBasketCommand
import pl.exbook.exbook.basket.domain.BasketValidationException
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferNotFoundException
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.TestData.otherSampleOfferId
import pl.exbook.exbook.shared.TestData.otherSampleSellerId
import pl.exbook.exbook.shared.TestData.otherSampleSellerUsername
import pl.exbook.exbook.shared.TestData.sampleBuyerId
import pl.exbook.exbook.shared.TestData.sampleBuyerUsername
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.shared.TestData.sampleSellerId
import pl.exbook.exbook.shared.TestData.sampleSellerUsername
import pl.exbook.exbook.user.domain.UserNotFoundException

class AddItemsToBasketSpec : ShouldSpec({
    val domain = BasketDomainAbility()

    context("add single offer to basket") {
        withData(
            OrderType.BUY,
            OrderType.EXCHANGE
        ) { orderType ->
            // given
            domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
            domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
            domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)

            val command = AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = orderType
            )

            // when
            domain.facade.addItemToBasket(command)
            val basket = domain.facade.getUserBasket(sampleBuyerId)

            // then
            basket.shouldNotBeNull()
            basket.userId shouldBe sampleBuyerId
            basket.itemsGroups shouldHaveSize 1

            // and
            val itemGroup = basket.itemsGroups.entries.first()

            itemGroup.key.orderType shouldBe orderType
            itemGroup.key.sellerId shouldBe sampleSellerId
            itemGroup.value shouldHaveSize 1

            // and
            val offer = itemGroup.value[0]

            offer.quantity shouldBeExactly 1
            offer.offer.id shouldBe sampleOfferId
        }
    }

    should("add two offers to the basket within one itemsGroup") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.thereIsOffer(offerId = otherSampleOfferId, sellerId = sampleSellerId)

        val firstOfferCommand = AddItemToBasketCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            quantity = 2,
            orderType = OrderType.BUY
        )

        val secondOfferCommand = AddItemToBasketCommand(
            username = sampleBuyerUsername,
            offerId = otherSampleOfferId,
            quantity = 1,
            orderType = OrderType.BUY
        )

        // when
        domain.facade.addItemToBasket(firstOfferCommand)
        domain.facade.addItemToBasket(secondOfferCommand)
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then
        basket.shouldNotBeNull()
        basket.userId shouldBe sampleBuyerId
        basket.itemsGroups shouldHaveSize 1

        // and
        val itemGroup = basket.itemsGroups.entries.first()

        itemGroup.key.sellerId shouldBe sampleSellerId
        itemGroup.key.orderType shouldBe OrderType.BUY
        itemGroup.value shouldHaveSize 2

        itemGroup.value[0].offer.id shouldBe sampleOfferId
        itemGroup.value[0].quantity shouldBeExactly 2
        itemGroup.value[1].offer.id shouldBe otherSampleOfferId
        itemGroup.value[1].quantity shouldBeExactly 1
    }

    should("create two itemsGroups when adding two times one offer to basket with different order type") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)

        val firstOfferCommand = AddItemToBasketCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            quantity = 1,
            orderType = OrderType.BUY
        )

        val secondOfferCommand = AddItemToBasketCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            quantity = 1,
            orderType = OrderType.EXCHANGE
        )

        // when
        domain.facade.addItemToBasket(firstOfferCommand)
        domain.facade.addItemToBasket(secondOfferCommand)
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then
        basket.shouldNotBeNull()
        basket.userId shouldBe sampleBuyerId
        basket.itemsGroups shouldHaveSize 2

        // and
        val itemGroup1 = basket.itemsGroups.entries.first()

        itemGroup1.key.sellerId shouldBe sampleSellerId
        itemGroup1.key.orderType shouldBe OrderType.BUY
        itemGroup1.value shouldHaveSize 1

        itemGroup1.value[0].offer.id shouldBe sampleOfferId
        itemGroup1.value[0].quantity shouldBeExactly 1

        // and
        val itemGroup2 = basket.itemsGroups.entries.toList()[1]

        itemGroup2.key.sellerId shouldBe sampleSellerId
        itemGroup2.key.orderType shouldBe OrderType.EXCHANGE
        itemGroup2.value shouldHaveSize 1

        itemGroup2.value[0].offer.id shouldBe sampleOfferId
        itemGroup2.value[0].quantity shouldBeExactly 1
    }

    context("create two itemsGroups when adding two offers to basket from other sellers") {
        withData(
            Pair(OrderType.BUY, OrderType.BUY),
            Pair(OrderType.BUY, OrderType.EXCHANGE),
            Pair(OrderType.EXCHANGE, OrderType.EXCHANGE),
            Pair(OrderType.EXCHANGE, OrderType.BUY)
        ) { orderType ->
            // given
            domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
            domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
            domain.thereIsUser(userId = otherSampleSellerId, username = otherSampleSellerUsername)
            domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
            domain.thereIsOffer(offerId = otherSampleOfferId, sellerId = otherSampleSellerId)

            val firstOfferCommand = AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = orderType.first
            )

            val secondOfferCommand = AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = otherSampleOfferId,
                quantity = 1,
                orderType = orderType.second
            )

            // when
            domain.facade.addItemToBasket(firstOfferCommand)
            domain.facade.addItemToBasket(secondOfferCommand)
            val basket = domain.facade.getUserBasket(sampleBuyerId)

            // then
            basket.shouldNotBeNull()
            basket.userId shouldBe sampleBuyerId
            basket.itemsGroups shouldHaveSize 2

            // and
            val itemGroup1 = basket.itemsGroups.entries.first()

            itemGroup1.key.sellerId shouldBe sampleSellerId
            itemGroup1.key.orderType shouldBe orderType.first
            itemGroup1.value shouldHaveSize 1

            itemGroup1.value[0].offer.id shouldBe sampleOfferId
            itemGroup1.value[0].quantity shouldBeExactly 1

            // and
            val itemGroup2 = basket.itemsGroups.entries.toList()[1]

            itemGroup2.key.sellerId shouldBe otherSampleSellerId
            itemGroup2.key.orderType shouldBe orderType.second
            itemGroup2.value shouldHaveSize 1

            itemGroup2.value[0].offer.id shouldBe otherSampleOfferId
            itemGroup2.value[0].quantity shouldBeExactly 1
        }
    }

    context("create four itemsGroups when adding to basket two offers with two different order types") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsUser(userId = otherSampleSellerId, username = otherSampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.thereIsOffer(offerId = otherSampleOfferId, sellerId = otherSampleSellerId)

        val firstItemCommand = AddItemToBasketCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            quantity = 1,
            orderType = OrderType.BUY
        )

        val secondItemCommand = AddItemToBasketCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            quantity = 1,
            orderType = OrderType.EXCHANGE
        )

        val thirdItemCommand = AddItemToBasketCommand(
            username = sampleBuyerUsername,
            offerId = otherSampleOfferId,
            quantity = 1,
            orderType = OrderType.BUY
        )

        val fourthItemCommand = AddItemToBasketCommand(
            username = sampleBuyerUsername,
            offerId = otherSampleOfferId,
            quantity = 1,
            orderType = OrderType.EXCHANGE
        )

        // when
        domain.facade.addItemToBasket(firstItemCommand)
        domain.facade.addItemToBasket(secondItemCommand)
        domain.facade.addItemToBasket(thirdItemCommand)
        domain.facade.addItemToBasket(fourthItemCommand)
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then
        basket.shouldNotBeNull()
        basket.userId shouldBe sampleBuyerId
        basket.itemsGroups shouldHaveSize 4

        // and
        val itemGroup1 = basket.itemsGroups.entries.first()

        itemGroup1.key.sellerId shouldBe sampleSellerId
        itemGroup1.key.orderType shouldBe OrderType.BUY
        itemGroup1.value shouldHaveSize 1

        itemGroup1.value[0].offer.id shouldBe sampleOfferId
        itemGroup1.value[0].quantity shouldBeExactly 1

        // and
        val itemGroup2 = basket.itemsGroups.entries.toList()[1]

        itemGroup2.key.sellerId shouldBe sampleSellerId
        itemGroup2.key.orderType shouldBe OrderType.EXCHANGE
        itemGroup2.value shouldHaveSize 1

        itemGroup2.value[0].offer.id shouldBe sampleOfferId
        itemGroup2.value[0].quantity shouldBeExactly 1

        // and
        val itemGroup3 = basket.itemsGroups.entries.toList()[2]

        itemGroup3.key.sellerId shouldBe otherSampleSellerId
        itemGroup3.key.orderType shouldBe OrderType.BUY
        itemGroup3.value shouldHaveSize 1

        itemGroup3.value[0].offer.id shouldBe otherSampleOfferId
        itemGroup3.value[0].quantity shouldBeExactly 1

        // and
        val itemGroup4 = basket.itemsGroups.entries.toList()[3]

        itemGroup4.key.sellerId shouldBe otherSampleSellerId
        itemGroup4.key.orderType shouldBe OrderType.EXCHANGE
        itemGroup4.value shouldHaveSize 1

        itemGroup4.value[0].offer.id shouldBe otherSampleOfferId
        itemGroup4.value[0].quantity shouldBeExactly 1
    }

    should("increment item quantity when trying to add item that already exist in basket") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)

        val firstItemCommand = AddItemToBasketCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            quantity = 1,
            orderType = OrderType.BUY
        )

        val secondItemCommand = AddItemToBasketCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            quantity = 1,
            orderType = OrderType.EXCHANGE
        )

        // when
        domain.facade.addItemToBasket(firstItemCommand)
        domain.facade.addItemToBasket(secondItemCommand)
        domain.facade.addItemToBasket(firstItemCommand)
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then
        basket.shouldNotBeNull()
        basket.userId shouldBe sampleBuyerId
        basket.itemsGroups shouldHaveSize 2

        // and
        val itemGroup1 = basket.itemsGroups.entries.first()

        itemGroup1.key.sellerId shouldBe sampleSellerId
        itemGroup1.key.orderType shouldBe OrderType.BUY
        itemGroup1.value shouldHaveSize 1

        itemGroup1.value[0].offer.id shouldBe sampleOfferId
        itemGroup1.value[0].quantity shouldBeExactly 2

        // and
        val itemGroup2 = basket.itemsGroups.entries.toList()[1]

        itemGroup2.key.sellerId shouldBe sampleSellerId
        itemGroup2.key.orderType shouldBe OrderType.EXCHANGE
        itemGroup2.value shouldHaveSize 1

        itemGroup2.value[0].offer.id shouldBe sampleOfferId
        itemGroup2.value[0].quantity shouldBeExactly 1
    }

    should("throw exception when tried to add to basket non existing offer") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsNoOfferFor(offerId = sampleOfferId)

        val command = AddItemToBasketCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            quantity = 1,
            orderType = OrderType.BUY
        )

        // then
        shouldThrowExactly<OfferNotFoundException> {
            domain.facade.addItemToBasket(command)
        }

        // when
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then: basket was not modified
        basket.itemsGroups shouldHaveSize 0
    }

    should("throw exception when tried to add item to basket for non existing user") {
        // given
        domain.thereIsNoUserFor(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsOffer(offerId = sampleOfferId)

        val command = AddItemToBasketCommand(
            username = sampleBuyerUsername,
            offerId = sampleOfferId,
            quantity = 1,
            orderType = OrderType.BUY
        )

        // then
        shouldThrowExactly<UserNotFoundException> {
            domain.facade.addItemToBasket(command)
        }
    }

    context("add offer to basket with order type that offer allows") {
        withData(
            Offer.Type.EXCHANGE_AND_BUY to OrderType.EXCHANGE,
            Offer.Type.EXCHANGE_AND_BUY to OrderType.BUY,
            Offer.Type.EXCHANGE_ONLY to OrderType.EXCHANGE,
            Offer.Type.BUY_ONLY to OrderType.BUY
        ) { (offerType, orderType) ->
            // given
            domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
            domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
            domain.thereIsOffer(offerId = sampleOfferId, type = offerType)

            val command = AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = orderType
            )

            // when
            domain.facade.addItemToBasket(command)
            val basket = domain.facade.getUserBasket(sampleBuyerId)

            // then
            basket.shouldNotBeNull()
            basket.userId shouldBe sampleBuyerId
            basket.itemsGroups shouldHaveSize 1

            // and
            val itemGroup = basket.itemsGroups.entries.first()

            itemGroup.key.orderType shouldBe orderType
            itemGroup.key.sellerId shouldBe sampleSellerId
            itemGroup.value shouldHaveSize 1

            // and
            val offer = itemGroup.value[0]

            offer.quantity shouldBeExactly 1
            offer.offer.id shouldBe sampleOfferId
        }
    }

    context("throw exception when tried to add offer to basket with order type that offer doesn't allow") {
        withData(
            Offer.Type.BUY_ONLY to OrderType.EXCHANGE,
            Offer.Type.EXCHANGE_ONLY to OrderType.BUY
        ) { (offerType, orderType) ->
            // given
            domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
            domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
            domain.thereIsOffer(offerId = sampleOfferId, type = offerType)

            val command = AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = orderType
            )

            // then
            shouldThrowExactly<BasketValidationException> {
                domain.facade.addItemToBasket(command)
            }
        }
    }

    context("not allow to add own offer to basket") {
        withData(
            OrderType.BUY,
            OrderType.EXCHANGE
        ) { orderType ->
            // given
            domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
            domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleBuyerId)

            val command = AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = orderType
            )

            // then
            shouldThrowExactly<BasketValidationException> {
                domain.facade.addItemToBasket(command)
            }
        }
    }

    context("throw error when trying to add item with incorrect quantity") {
        withData(
            -2L,
            -1L,
            0L
        ) { quantity ->
            // given
            domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
            domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
            domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)

            val command = AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = quantity,
                orderType = OrderType.BUY
            )

            // when
            shouldThrowExactly<BasketValidationException> {
                domain.facade.addItemToBasket(command)
            }
        }
    }
})

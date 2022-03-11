package pl.exbook.exbook.features.basket

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.BasketDomainAbility
import pl.exbook.exbook.basket.domain.AddItemToBasketCommand
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.pln
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.TestData.otherSampleImageUrl
import pl.exbook.exbook.shared.TestData.otherSampleOfferId
import pl.exbook.exbook.shared.TestData.otherSampleSellerId
import pl.exbook.exbook.shared.TestData.otherSampleSellerUsername
import pl.exbook.exbook.shared.TestData.sampleBuyerId
import pl.exbook.exbook.shared.TestData.sampleBuyerUsername
import pl.exbook.exbook.shared.TestData.sampleImageUrl
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.shared.TestData.sampleSellerId
import pl.exbook.exbook.shared.TestData.sampleSellerUsername
import pl.exbook.exbook.shared.TestData.thirdSampleOfferId

class DetailedBasketSpec : ShouldSpec({
    val domain = BasketDomainAbility()

    should("get empty detailed basket") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)

        // when
        val basket = domain.facade.getDetailedUserBasket(sampleBuyerUsername)

        // then
        basket.shouldNotBeNull()
        basket.userId shouldBe sampleBuyerId
        basket.itemsGroups.shouldBeEmpty()
        basket.totalOffersCost shouldBeEqualComparingTo Money.zeroPln()
    }

    should("detailed basket should be the same as basket") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)

        // when
        val basket = domain.facade.getUserBasket(sampleBuyerId)
        val detailedBasket = domain.facade.getDetailedUserBasket(sampleBuyerUsername)

        // then
        basket.id shouldBe detailedBasket.id
        basket.userId shouldBe detailedBasket.userId
        basket.itemsGroups.entries shouldHaveSize basket.itemsGroups.size
    }

    should("calculate all prices") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername, firstName = "Jan", lastName = "Kowalski")
        domain.thereIsUser(userId = otherSampleSellerId, username = otherSampleSellerUsername, firstName = "Tom", lastName = "Riddle")

        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId, price = "10.99".pln())
        domain.thereIsOffer(offerId = otherSampleOfferId, sellerId = otherSampleSellerId, price = "8.99".pln())
        domain.thereIsOffer(offerId = thirdSampleOfferId, sellerId = otherSampleSellerId, price = "4.99".pln())

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
                quantity = 2,
                orderType = OrderType.EXCHANGE
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
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = otherSampleOfferId,
                quantity = 4,
                orderType = OrderType.EXCHANGE
            )
        )
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = thirdSampleOfferId,
                quantity = 5,
                orderType = OrderType.BUY
            )
        )

        // when
        val basket = domain.facade.getDetailedUserBasket(sampleBuyerUsername)

        // then
        basket.itemsGroups shouldHaveSize 4
        basket.totalOffersCost shouldBeEqualComparingTo "62.91".pln()

        // and
        basket.itemsGroups[0].seller.id shouldBe sampleSellerId
        basket.itemsGroups[0].orderType shouldBe OrderType.BUY
        basket.itemsGroups[0].items shouldHaveSize 1
        basket.itemsGroups[0].items[0].offer.id shouldBe sampleOfferId
        basket.itemsGroups[0].groupTotalOffersPrice shouldBeEqualComparingTo "10.99".pln()

        // and
        basket.itemsGroups[1].seller.id shouldBe sampleSellerId
        basket.itemsGroups[1].orderType shouldBe OrderType.EXCHANGE
        basket.itemsGroups[1].items shouldHaveSize 1
        basket.itemsGroups[1].items[0].offer.id shouldBe sampleOfferId
        basket.itemsGroups[1].groupTotalOffersPrice shouldBeEqualComparingTo "0.00".pln()

        // and
        basket.itemsGroups[2].seller.id shouldBe otherSampleSellerId
        basket.itemsGroups[2].orderType shouldBe OrderType.BUY
        basket.itemsGroups[2].items shouldHaveSize 2
        basket.itemsGroups[2].items[0].offer.id shouldBe otherSampleOfferId
        basket.itemsGroups[2].items[1].offer.id shouldBe thirdSampleOfferId
        basket.itemsGroups[2].groupTotalOffersPrice shouldBeEqualComparingTo "51.92".pln()

        // and
        basket.itemsGroups[3].seller.id shouldBe otherSampleSellerId
        basket.itemsGroups[3].orderType shouldBe OrderType.EXCHANGE
        basket.itemsGroups[3].items shouldHaveSize 1
        basket.itemsGroups[3].items[0].offer.id shouldBe otherSampleOfferId
        basket.itemsGroups[3].groupTotalOffersPrice shouldBeEqualComparingTo "0.00".pln()
    }

    should("decorate all items") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername, firstName = "Jan", lastName = "Kowalski")
        domain.thereIsUser(userId = otherSampleSellerId, username = otherSampleSellerUsername, firstName = "Tom", lastName = "Riddle")

        domain.thereIsOffer(
            offerId = sampleOfferId,
            sellerId = sampleSellerId,
            bookAuthor = "Tadeusz",
            bookTitle = "Wierzba",
            thumbnailUrl = sampleImageUrl,
            allImagesUrls = listOf(sampleImageUrl),
            price = "10.99".pln()
        )
        domain.thereIsOffer(
            offerId = otherSampleOfferId,
            sellerId = otherSampleSellerId,
            bookAuthor = "Jaro",
            bookTitle = "Michałowo",
            thumbnailUrl = otherSampleImageUrl,
            allImagesUrls = listOf(otherSampleImageUrl),
            price = "12.99".pln()
        )

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
                quantity = 4,
                orderType = OrderType.EXCHANGE
            )
        )

        // when
        val basket = domain.facade.getDetailedUserBasket(sampleBuyerUsername)

        // then
        basket.userId shouldBe sampleBuyerId
        basket.totalOffersCost shouldBeEqualComparingTo "21.98".pln()
        basket.itemsGroups shouldHaveSize 2

        // and
        basket.itemsGroups[0].seller.id shouldBe sampleSellerId
        basket.itemsGroups[0].seller.firstName shouldBe "Jan"
        basket.itemsGroups[0].seller.lastName shouldBe "Kowalski"
        basket.itemsGroups[0].orderType shouldBe OrderType.BUY
        basket.itemsGroups[0].groupTotalOffersPrice shouldBeEqualComparingTo "21.98".pln()
        basket.itemsGroups[0].items shouldHaveSize 1
        basket.itemsGroups[0].items[0].offer.id shouldBe sampleOfferId
        basket.itemsGroups[0].items[0].offer.seller.id shouldBe sampleSellerId
        basket.itemsGroups[0].items[0].offer.seller.firstName shouldBe "Jan"
        basket.itemsGroups[0].items[0].offer.seller.lastName shouldBe "Kowalski"
        basket.itemsGroups[0].items[0].offer.book.author shouldBe "Tadeusz"
        basket.itemsGroups[0].items[0].offer.book.title shouldBe "Wierzba"
        basket.itemsGroups[0].items[0].offer.images.thumbnail.shouldNotBeNull()
        basket.itemsGroups[0].items[0].offer.images.thumbnail?.url shouldBe sampleImageUrl
        basket.itemsGroups[0].items[0].offer.images.allImages shouldHaveSize 1
        basket.itemsGroups[0].items[0].offer.images.allImages[0].url shouldBe sampleImageUrl
        basket.itemsGroups[0].items[0].offer.price!! shouldBeEqualComparingTo "10.99".pln()
        basket.itemsGroups[0].items[0].quantity shouldBeExactly 2
        basket.itemsGroups[0].items[0].totalPrice shouldBeEqualComparingTo "21.98".pln()

        // and
        basket.itemsGroups[1].seller.id shouldBe otherSampleSellerId
        basket.itemsGroups[1].seller.firstName shouldBe "Tom"
        basket.itemsGroups[1].seller.lastName shouldBe "Riddle"
        basket.itemsGroups[1].orderType shouldBe OrderType.EXCHANGE
        basket.itemsGroups[1].groupTotalOffersPrice shouldBeEqualComparingTo "0.00".pln()
        basket.itemsGroups[1].items shouldHaveSize 1
        basket.itemsGroups[1].items[0].offer.id shouldBe otherSampleOfferId
        basket.itemsGroups[1].items[0].offer.seller.id shouldBe otherSampleSellerId
        basket.itemsGroups[1].items[0].offer.seller.firstName shouldBe "Tom"
        basket.itemsGroups[1].items[0].offer.seller.lastName shouldBe "Riddle"
        basket.itemsGroups[1].items[0].offer.book.author shouldBe "Jaro"
        basket.itemsGroups[1].items[0].offer.book.title shouldBe "Michałowo"
        basket.itemsGroups[1].items[0].offer.images.thumbnail.shouldNotBeNull()
        basket.itemsGroups[1].items[0].offer.images.thumbnail?.url shouldBe otherSampleImageUrl
        basket.itemsGroups[1].items[0].offer.images.allImages shouldHaveSize 1
        basket.itemsGroups[1].items[0].offer.images.allImages[0].url shouldBe otherSampleImageUrl
        basket.itemsGroups[1].items[0].offer.price!! shouldBeEqualComparingTo "0.00".pln()
        basket.itemsGroups[1].items[0].quantity shouldBeExactly 4
        basket.itemsGroups[1].items[0].totalPrice shouldBeEqualComparingTo "0.00".pln()
    }
})

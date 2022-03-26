package pl.exbook.exbook.features.baskettransaction

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.BasketTransactionAbility
import pl.exbook.exbook.baskettransaction.domain.PreviewPurchaseCommand
import pl.exbook.exbook.mock.IdGenerationStrategy
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.pln
import pl.exbook.exbook.shared.TestData.otherSampleOfferId
import pl.exbook.exbook.shared.TestData.otherSampleSellerId
import pl.exbook.exbook.shared.TestData.otherSampleSellerUsername
import pl.exbook.exbook.shared.TestData.otherSampleShippingMethodId
import pl.exbook.exbook.shared.TestData.sampleAddress
import pl.exbook.exbook.shared.TestData.sampleAuthor
import pl.exbook.exbook.shared.TestData.sampleBasketId
import pl.exbook.exbook.shared.TestData.sampleBuyerEmail
import pl.exbook.exbook.shared.TestData.sampleBuyerFirstName
import pl.exbook.exbook.shared.TestData.sampleBuyerId
import pl.exbook.exbook.shared.TestData.sampleBuyerLastName
import pl.exbook.exbook.shared.TestData.sampleBuyerPhoneNumber
import pl.exbook.exbook.shared.TestData.sampleBuyerUsername
import pl.exbook.exbook.shared.TestData.sampleCity
import pl.exbook.exbook.shared.TestData.sampleCountry
import pl.exbook.exbook.shared.TestData.sampleIsbn
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.shared.TestData.samplePickupPointId
import pl.exbook.exbook.shared.TestData.samplePostalCost
import pl.exbook.exbook.shared.TestData.sampleSellerId
import pl.exbook.exbook.shared.TestData.sampleSellerUsername
import pl.exbook.exbook.shared.TestData.sampleShippingMethodId
import pl.exbook.exbook.shared.TestData.sampleTitle
import pl.exbook.exbook.shared.TestData.tenPln

class PreviewBasketSpec : ShouldSpec({
    val domain = BasketTransactionAbility()

    should("preview purchase with one order with one offer with buy type without shipping") {
        // given
        domain.thereIsUser {
            userId = sampleBuyerId
            username = sampleBuyerUsername
        }
        domain.thereIsUser {
            userId = sampleSellerId
            username = sampleSellerUsername
        }
        domain.thereIsOffer {
            price = tenPln
        }
        domain.thereIsBasket {
            id = sampleBasketId
            buyerId = sampleBuyerId
            username = sampleBuyerUsername
            itemsGroup {
                orderType = OrderType.BUY
                item {
                    quantity = 2
                }
            }
        }
        domain.willPreviewAvailableShipping { }


        val command = PreviewPurchaseCommand(
            orders = listOf()
        )

        // when
        val result = domain.facade.previewPurchase(sampleBuyerUsername, command)

        // then
        result.buyer.id shouldBe sampleBuyerId
        result.totalOffersPrice shouldBeEqualComparingTo "20.00".pln()
        result.totalPrice shouldBeEqualComparingTo "20.00".pln()
        result.orders shouldHaveSize 1
        result.orders[0].shouldNotBeNull()
        result.orders[0].orderType shouldBe OrderType.BUY
        result.orders[0].exchangeBooks.shouldBeEmpty()
        result.orders[0].shipping.shouldBeNull()
        result.orders[0].totalOffersPrice shouldBeEqualComparingTo "20.00".pln()
        result.orders[0].totalPrice shouldBeEqualComparingTo "20.00".pln()
        result.orders[0].items shouldHaveSize 1
        result.orders[0].items[0].offer.id shouldBe sampleOfferId
        result.orders[0].items[0].offer.book.author shouldBe sampleAuthor
        result.orders[0].items[0].offer.book.isbn shouldBe sampleIsbn
        result.orders[0].items[0].offer.book.condition shouldBe Offer.Condition.NEW
        result.orders[0].items[0].offer.book.title shouldBe sampleTitle
        result.orders[0].items[0].offer.price.shouldNotBeNull()
        result.orders[0].items[0].offer.price!! shouldBeEqualComparingTo tenPln
        result.orders[0].items[0].totalPrice shouldBeEqualComparingTo "20.00".pln()
    }

    should("preview purchase with one order with one offer with buy type with shipping") {
        // given
        domain.thereIsUser {
            userId = sampleBuyerId
            username = sampleBuyerUsername
        }
        domain.thereIsUser {
            userId = sampleSellerId
            username = sampleSellerUsername
        }
        domain.thereIsOffer {
            price = tenPln
            shippingMethod {
                id = sampleShippingMethodId
                price = "6.00".pln()
            }
        }
        domain.thereIsBasket {
            id = sampleBasketId
            buyerId = sampleBuyerId
            username = sampleBuyerUsername
            itemsGroup {
                orderType = OrderType.BUY
                item {
                    quantity = 2
                }
            }
        }
        domain.thereIsShippingMethod {
            id = sampleShippingMethodId
            defaultCost = "5.00".pln()
        }
        domain.willCreateShipping(IdGenerationStrategy.RANDOM) {
            finalCost = "6.00".pln()
            pickupPoint { }
        }
        domain.willPreviewAvailableShipping { }

        val command = PreviewPurchaseCommand(
            orders = listOf(
                PreviewPurchaseCommand.Order(
                    sellerId = sampleSellerId,
                    orderType = OrderType.BUY,
                    shipping = PreviewPurchaseCommand.Shipping(
                        shippingMethodId = sampleShippingMethodId,
                        pickupPoint = PreviewPurchaseCommand.PickupPoint(
                            firstAndLastName = sampleBuyerFirstName + sampleBuyerLastName,
                            phoneNumber = sampleBuyerPhoneNumber,
                            sampleBuyerEmail,
                            pickupPointId = samplePickupPointId
                        ),
                        shippingAddress = null
                    )
                )
            )
        )

        // when
        val result = domain.facade.previewPurchase(sampleBuyerUsername, command)

        // then
        result.buyer.id shouldBe sampleBuyerId
        result.totalOffersPrice shouldBeEqualComparingTo "20.00".pln()
        result.totalPrice shouldBeEqualComparingTo "26.00".pln()

        result.orders shouldHaveSize 1
        result.orders[0].shouldNotBeNull()
        result.orders[0].orderType shouldBe OrderType.BUY
        result.orders[0].exchangeBooks.shouldBeEmpty()

        result.orders[0].shipping.shouldNotBeNull()
        result.orders[0].shipping!!.pickupPoint.shouldNotBeNull()
        result.orders[0].shipping!!.shippingAddress.shouldBeNull()
        result.orders[0].shipping!!.shippingMethod.id shouldBe sampleShippingMethodId
        result.orders[0].shipping!!.shippingMethod.price.finalPrice shouldBeEqualToComparingFields "6.00".pln()
        result.orders[0].shipping!!.pickupPoint!!.pickupPointId shouldBe samplePickupPointId

        result.orders[0].totalOffersPrice shouldBeEqualComparingTo "20.00".pln()
        result.orders[0].totalPrice shouldBeEqualComparingTo "26.00".pln()

        result.orders[0].items shouldHaveSize 1
        result.orders[0].items[0].offer.id shouldBe sampleOfferId
        result.orders[0].items[0].offer.book.author shouldBe sampleAuthor
        result.orders[0].items[0].offer.book.isbn shouldBe sampleIsbn
        result.orders[0].items[0].offer.book.condition shouldBe Offer.Condition.NEW
        result.orders[0].items[0].offer.book.title shouldBe sampleTitle
        result.orders[0].items[0].offer.price.shouldNotBeNull()
        result.orders[0].items[0].offer.price!! shouldBeEqualComparingTo tenPln
        result.orders[0].items[0].totalPrice shouldBeEqualComparingTo "20.00".pln()
    }

    should("preview purchase with one order with two offers with shipping") {
        // given
        domain.thereIsUser {
            userId = sampleBuyerId
            username = sampleBuyerUsername
        }
        domain.thereIsUser {
            userId = sampleSellerId
            username = sampleSellerUsername
        }
        domain.thereIsOffer {
            id = sampleOfferId
            price = tenPln
        }
        domain.thereIsOffer {
            id = otherSampleOfferId
            price = "12.00".pln()
        }
        domain.thereIsBasket {
            id = sampleBasketId
            buyerId = sampleBuyerId
            username = sampleBuyerUsername
            itemsGroup {
                orderType = OrderType.BUY
                item {
                    offerId = sampleOfferId
                    quantity = 2
                }
                item {
                    offerId = otherSampleOfferId
                    quantity = 1
                }
            }
        }
        domain.thereIsShippingMethod {
            id = sampleShippingMethodId
            defaultCost = "5.00".pln()
        }
        domain.willCreateShipping(IdGenerationStrategy.RANDOM) {
            finalCost = "6.00".pln()
            address { }
        }
        domain.willPreviewAvailableShipping { }

        val command = PreviewPurchaseCommand(
            orders = listOf(
                PreviewPurchaseCommand.Order(
                    sellerId = sampleSellerId,
                    orderType = OrderType.BUY,
                    shipping = PreviewPurchaseCommand.Shipping(
                        shippingMethodId = sampleShippingMethodId,
                        shippingAddress = PreviewPurchaseCommand.ShippingAddress(
                            firstAndLastName = sampleBuyerFirstName + sampleBuyerLastName,
                            phoneNumber = sampleBuyerPhoneNumber,
                            email = sampleBuyerEmail,
                            address = sampleAddress,
                            postalCode = samplePostalCost,
                            city = sampleCity,
                            country = sampleCountry
                        ),
                        pickupPoint = null
                    )
                )
            )
        )

        // when
        val result = domain.facade.previewPurchase(sampleBuyerUsername, command)

        // then
        result.buyer.id shouldBe sampleBuyerId
        result.totalOffersPrice shouldBeEqualComparingTo "32.00".pln()
        result.totalPrice shouldBeEqualComparingTo "38.00".pln()

        result.orders shouldHaveSize 1
        result.orders[0].shouldNotBeNull()
        result.orders[0].orderType shouldBe OrderType.BUY
        result.orders[0].exchangeBooks.shouldBeEmpty()

        result.orders[0].shipping.shouldNotBeNull()
        result.orders[0].shipping!!.pickupPoint.shouldBeNull()
        result.orders[0].shipping!!.shippingAddress.shouldNotBeNull()
        result.orders[0].shipping!!.shippingMethod.id shouldBe sampleShippingMethodId
        result.orders[0].shipping!!.shippingMethod.price.finalPrice shouldBeEqualToComparingFields "6.00".pln()
        result.orders[0].shipping!!.shippingAddress!!.address shouldBe sampleAddress

        result.orders[0].totalOffersPrice shouldBeEqualComparingTo "32.00".pln()
        result.orders[0].totalPrice shouldBeEqualComparingTo "38.00".pln()

        result.orders[0].items shouldHaveSize 2
        result.orders[0].items[0].offer.id shouldBe sampleOfferId
        result.orders[0].items[0].offer.price.shouldNotBeNull()
        result.orders[0].items[0].offer.price!! shouldBeEqualComparingTo tenPln
        result.orders[0].items[0].quantity shouldBe 2
        result.orders[0].items[0].totalPrice shouldBeEqualComparingTo "20.00".pln()

        result.orders[0].items[1].offer.id shouldBe otherSampleOfferId
        result.orders[0].items[1].offer.price.shouldNotBeNull()
        result.orders[0].items[1].offer.price!! shouldBeEqualComparingTo "12.00".pln()
        result.orders[0].items[1].quantity shouldBe 1
        result.orders[0].items[1].totalPrice shouldBeEqualComparingTo "12.00".pln()
    }

    should("preview purchase with two orders from the same seller with shipping") {
        // given
        domain.thereIsUser {
            userId = sampleBuyerId
            username = sampleBuyerUsername
        }
        domain.thereIsUser {
            userId = sampleSellerId
            username = sampleSellerUsername
        }
        domain.thereIsOffer {
            id = sampleOfferId
            price = tenPln
        }
        domain.thereIsBasket {
            id = sampleBasketId
            buyerId = sampleBuyerId
            username = sampleBuyerUsername
            itemsGroup {
                orderType = OrderType.BUY
                item {
                    offerId = sampleOfferId
                    quantity = 2
                }
            }
            itemsGroup {
                orderType = OrderType.EXCHANGE
                item {
                    offerId = sampleOfferId
                }
                exchangeBook { }
            }
        }
        domain.thereIsShippingMethod {
            id = sampleShippingMethodId
            pickupPointMethod = true
            defaultCost = "5.00".pln()
        }
        domain.thereIsShippingMethod {
            id = otherSampleShippingMethodId
            pickupPointMethod = false
            defaultCost = "5.00".pln()
        }
        domain.willCreateShipping(IdGenerationStrategy.RANDOM) {
            finalCost = "5.00".pln()
            address { }
        }
        domain.willPreviewAvailableShipping { }

        val command = PreviewPurchaseCommand(
            orders = listOf(
                PreviewPurchaseCommand.Order(
                    sellerId = sampleSellerId,
                    orderType = OrderType.BUY,
                    shipping = PreviewPurchaseCommand.Shipping(
                        shippingMethodId = sampleShippingMethodId,
                        pickupPoint = PreviewPurchaseCommand.PickupPoint(
                            firstAndLastName = sampleBuyerFirstName + sampleBuyerLastName,
                            phoneNumber = sampleBuyerPhoneNumber,
                            sampleBuyerEmail,
                            pickupPointId = samplePickupPointId
                        ),
                        shippingAddress = null
                    )
                ),
                PreviewPurchaseCommand.Order(
                    sellerId = sampleSellerId,
                    orderType = OrderType.EXCHANGE,
                    shipping = PreviewPurchaseCommand.Shipping(
                        shippingMethodId = otherSampleShippingMethodId,
                        pickupPoint = null,
                        shippingAddress = PreviewPurchaseCommand.ShippingAddress(
                            firstAndLastName = sampleBuyerFirstName + sampleBuyerLastName,
                            phoneNumber = sampleBuyerPhoneNumber,
                            email = sampleBuyerEmail,
                            address = sampleAddress,
                            postalCode = samplePostalCost,
                            city = sampleCity,
                            country = sampleCountry
                        )
                    )
                )
            )
        )

        // when
        val result = domain.facade.previewPurchase(sampleBuyerUsername, command)

        // then
        result.buyer.id shouldBe sampleBuyerId
        result.totalOffersPrice shouldBeEqualComparingTo "20.00".pln()
        result.totalPrice shouldBeEqualComparingTo "30.00".pln()

        result.orders shouldHaveSize 2
        result.orders[0].shouldNotBeNull()
        result.orders[0].orderType shouldBe OrderType.BUY
        result.orders[0].exchangeBooks.shouldBeEmpty()
        result.orders[0].totalOffersPrice shouldBeEqualComparingTo "20.00".pln()
        result.orders[0].totalPrice shouldBeEqualComparingTo "25.00".pln()

        result.orders[0].shipping.shouldNotBeNull()
        result.orders[0].shipping!!.pickupPoint.shouldNotBeNull()
        result.orders[0].shipping!!.shippingAddress.shouldBeNull()
        result.orders[0].shipping!!.shippingMethod.id shouldBe sampleShippingMethodId
        result.orders[0].shipping!!.shippingMethod.price.finalPrice shouldBeEqualToComparingFields "5.00".pln()

        result.orders[0].items shouldHaveSize 1
        result.orders[0].items[0].offer.id shouldBe sampleOfferId
        result.orders[0].items[0].offer.price.shouldNotBeNull()
        result.orders[0].items[0].offer.price!! shouldBeEqualComparingTo tenPln
        result.orders[0].items[0].quantity shouldBe 2
        result.orders[0].items[0].totalPrice shouldBeEqualComparingTo "20.00".pln()

        result.orders[1].shouldNotBeNull()
        result.orders[1].orderType shouldBe OrderType.EXCHANGE
        result.orders[1].exchangeBooks shouldHaveSize 1
        result.orders[1].totalOffersPrice shouldBeEqualComparingTo "0.00".pln()
        result.orders[1].totalPrice shouldBeEqualComparingTo "5.00".pln()

        result.orders[1].shipping.shouldNotBeNull()
        result.orders[1].shipping!!.pickupPoint.shouldBeNull()
        result.orders[1].shipping!!.shippingAddress.shouldNotBeNull()
        result.orders[1].shipping!!.shippingMethod.id shouldBe otherSampleShippingMethodId
        result.orders[1].shipping!!.shippingMethod.price.finalPrice shouldBeEqualToComparingFields "5.00".pln()

        result.orders[1].items shouldHaveSize 1
        result.orders[1].items[0].offer.id shouldBe sampleOfferId
        result.orders[1].items[0].offer.price.shouldNotBeNull()
        result.orders[1].items[0].offer.price!! shouldBeEqualComparingTo "0.00".pln()
        result.orders[1].items[0].quantity shouldBe 1
        result.orders[1].items[0].totalPrice shouldBeEqualComparingTo "0.00".pln()
    }

    should("preview purchase with two orders from different sellers") {
        // given
        domain.thereIsUser {
            userId = sampleBuyerId
            username = sampleBuyerUsername
        }
        domain.thereIsUser {
            userId = sampleSellerId
            username = sampleSellerUsername
        }
        domain.thereIsUser {
            userId = otherSampleSellerId
            username = otherSampleSellerUsername
        }
        domain.thereIsOffer {
            id = sampleOfferId
            sellerId = sampleSellerId
            price = tenPln
        }
        domain.thereIsOffer {
            id = otherSampleOfferId
            sellerId = otherSampleSellerId
            price = "20.00".pln()
        }
        domain.thereIsBasket {
            id = sampleBasketId
            buyerId = sampleBuyerId
            username = sampleBuyerUsername
            itemsGroup {
                sellerId = sampleSellerId
                orderType = OrderType.BUY
                item {
                    offerId = sampleOfferId
                    quantity = 2
                }
            }
            itemsGroup {
                sellerId = otherSampleSellerId
                orderType = OrderType.BUY
                item {
                    offerId = otherSampleOfferId
                    quantity = 5
                }
            }
        }
        domain.thereIsShippingMethod {
            id = sampleShippingMethodId
            pickupPointMethod = true
            defaultCost = "5.00".pln()
        }
        domain.thereIsShippingMethod {
            id = otherSampleShippingMethodId
            pickupPointMethod = false
            defaultCost = "5.00".pln()
        }
        domain.willCreateShipping(IdGenerationStrategy.RANDOM) {
            finalCost = "5.00".pln()
            address { }
            pickupPoint { }
        }
        domain.willPreviewAvailableShipping { }

        val command = PreviewPurchaseCommand(
            orders = listOf(
                PreviewPurchaseCommand.Order(
                    sellerId = sampleSellerId,
                    orderType = OrderType.BUY,
                    shipping = PreviewPurchaseCommand.Shipping(
                        shippingMethodId = sampleShippingMethodId,
                        pickupPoint = PreviewPurchaseCommand.PickupPoint(
                            firstAndLastName = sampleBuyerFirstName + sampleBuyerLastName,
                            phoneNumber = sampleBuyerPhoneNumber,
                            sampleBuyerEmail,
                            pickupPointId = samplePickupPointId
                        ),
                        shippingAddress = null
                    )
                ),
                PreviewPurchaseCommand.Order(
                    sellerId = otherSampleSellerId,
                    orderType = OrderType.BUY,
                    shipping = PreviewPurchaseCommand.Shipping(
                        shippingMethodId = otherSampleShippingMethodId,
                        pickupPoint = null,
                        shippingAddress = PreviewPurchaseCommand.ShippingAddress(
                            firstAndLastName = sampleBuyerFirstName + sampleBuyerLastName,
                            phoneNumber = sampleBuyerPhoneNumber,
                            email = sampleBuyerEmail,
                            address = sampleAddress,
                            postalCode = samplePostalCost,
                            city = sampleCity,
                            country = sampleCountry
                        )
                    )
                )
            )
        )

        // when
        val result = domain.facade.previewPurchase(sampleBuyerUsername, command)

        // then
        result.buyer.id shouldBe sampleBuyerId
        result.totalOffersPrice shouldBeEqualComparingTo "120.00".pln()
        result.totalPrice shouldBeEqualComparingTo "130.00".pln()

        result.orders shouldHaveSize 2
        result.orders[0].shouldNotBeNull()
        result.orders[0].seller.id shouldBe sampleSellerId
        result.orders[0].orderType shouldBe OrderType.BUY
        result.orders[0].exchangeBooks.shouldBeEmpty()
        result.orders[0].totalOffersPrice shouldBeEqualComparingTo "20.00".pln()
        result.orders[0].totalPrice shouldBeEqualComparingTo "25.00".pln()

        result.orders[0].shipping.shouldNotBeNull()
        result.orders[0].shipping!!.pickupPoint.shouldNotBeNull()
        result.orders[0].shipping!!.shippingAddress.shouldBeNull()
        result.orders[0].shipping!!.shippingMethod.id shouldBe sampleShippingMethodId
        result.orders[0].shipping!!.shippingMethod.price.finalPrice shouldBeEqualToComparingFields "5.00".pln()

        result.orders[0].items shouldHaveSize 1
        result.orders[0].items[0].offer.id shouldBe sampleOfferId
        result.orders[0].items[0].offer.price.shouldNotBeNull()
        result.orders[0].items[0].offer.price!! shouldBeEqualComparingTo tenPln
        result.orders[0].items[0].quantity shouldBe 2
        result.orders[0].items[0].totalPrice shouldBeEqualComparingTo "20.00".pln()

        result.orders[1].shouldNotBeNull()
        result.orders[1].seller.id shouldBe otherSampleSellerId
        result.orders[1].orderType shouldBe OrderType.BUY
        result.orders[1].exchangeBooks.shouldBeEmpty()
        result.orders[1].totalOffersPrice shouldBeEqualComparingTo "100.00".pln()
        result.orders[1].totalPrice shouldBeEqualComparingTo "105.00".pln()

        result.orders[1].shipping.shouldNotBeNull()
        result.orders[1].shipping!!.pickupPoint.shouldBeNull()
        result.orders[1].shipping!!.shippingAddress.shouldNotBeNull()
        result.orders[1].shipping!!.shippingMethod.id shouldBe otherSampleShippingMethodId
        result.orders[1].shipping!!.shippingMethod.price.finalPrice shouldBeEqualToComparingFields "5.00".pln()

        result.orders[1].items shouldHaveSize 1
        result.orders[1].items[0].offer.id shouldBe otherSampleOfferId
        result.orders[1].items[0].offer.price.shouldNotBeNull()
        result.orders[1].items[0].offer.price!! shouldBeEqualComparingTo "20.00".pln()
        result.orders[1].items[0].quantity shouldBe 5
        result.orders[1].items[0].totalPrice shouldBeEqualComparingTo "100.00".pln()
    }

    should("preview purchase with only one shipping on two orders") {
        // given
        domain.thereIsUser {
            userId = sampleBuyerId
            username = sampleBuyerUsername
        }
        domain.thereIsUser {
            userId = sampleSellerId
            username = sampleSellerUsername
        }
        domain.thereIsUser {
            userId = otherSampleSellerId
            username = otherSampleSellerUsername
        }
        domain.thereIsOffer {
            id = sampleOfferId
            sellerId = sampleSellerId
            price = tenPln
        }
        domain.thereIsOffer {
            id = otherSampleOfferId
            sellerId = otherSampleSellerId
            price = "20.00".pln()
        }
        domain.thereIsBasket {
            id = sampleBasketId
            buyerId = sampleBuyerId
            username = sampleBuyerUsername
            itemsGroup {
                sellerId = sampleSellerId
                orderType = OrderType.BUY
                item {
                    offerId = sampleOfferId
                    quantity = 2
                }
            }
            itemsGroup {
                sellerId = otherSampleSellerId
                orderType = OrderType.BUY
                item {
                    offerId = otherSampleOfferId
                    quantity = 5
                }
            }
        }
        domain.thereIsShippingMethod {
            id = sampleShippingMethodId
            pickupPointMethod = true
            defaultCost = "5.00".pln()
        }
        domain.willCreateShipping(IdGenerationStrategy.RANDOM) {
            finalCost = "5.00".pln()
            pickupPoint { }
        }
        domain.willPreviewAvailableShipping { }

        val command = PreviewPurchaseCommand(
            orders = listOf(
                PreviewPurchaseCommand.Order(
                    sellerId = sampleSellerId,
                    orderType = OrderType.BUY,
                    shipping = PreviewPurchaseCommand.Shipping(
                        shippingMethodId = sampleShippingMethodId,
                        pickupPoint = PreviewPurchaseCommand.PickupPoint(
                            firstAndLastName = sampleBuyerFirstName + sampleBuyerLastName,
                            phoneNumber = sampleBuyerPhoneNumber,
                            sampleBuyerEmail,
                            pickupPointId = samplePickupPointId
                        ),
                        shippingAddress = null
                    )
                )
            )
        )

        // when
        val result = domain.facade.previewPurchase(sampleBuyerUsername, command)

        // then
        result.buyer.id shouldBe sampleBuyerId
        result.totalOffersPrice shouldBeEqualComparingTo "120.00".pln()
        result.totalPrice shouldBeEqualComparingTo "125.00".pln()

        result.orders shouldHaveSize 2
        result.orders[0].shouldNotBeNull()
        result.orders[0].seller.id shouldBe sampleSellerId
        result.orders[0].orderType shouldBe OrderType.BUY
        result.orders[0].exchangeBooks.shouldBeEmpty()
        result.orders[0].totalOffersPrice shouldBeEqualComparingTo "20.00".pln()
        result.orders[0].totalPrice shouldBeEqualComparingTo "25.00".pln()

        result.orders[0].shipping.shouldNotBeNull()
        result.orders[0].shipping!!.pickupPoint.shouldNotBeNull()
        result.orders[0].shipping!!.shippingAddress.shouldBeNull()
        result.orders[0].shipping!!.shippingMethod.id shouldBe sampleShippingMethodId
        result.orders[0].shipping!!.shippingMethod.price.finalPrice shouldBeEqualToComparingFields "5.00".pln()

        result.orders[0].items shouldHaveSize 1
        result.orders[0].items[0].offer.id shouldBe sampleOfferId
        result.orders[0].items[0].offer.price.shouldNotBeNull()
        result.orders[0].items[0].offer.price!! shouldBeEqualComparingTo tenPln
        result.orders[0].items[0].quantity shouldBe 2
        result.orders[0].items[0].totalPrice shouldBeEqualComparingTo "20.00".pln()

        result.orders[1].shouldNotBeNull()
        result.orders[1].seller.id shouldBe otherSampleSellerId
        result.orders[1].orderType shouldBe OrderType.BUY
        result.orders[1].exchangeBooks.shouldBeEmpty()
        result.orders[1].totalOffersPrice shouldBeEqualComparingTo "100.00".pln()
        result.orders[1].totalPrice shouldBeEqualComparingTo "100.00".pln()

        result.orders[1].shipping.shouldBeNull()

        result.orders[1].items shouldHaveSize 1
        result.orders[1].items[0].offer.id shouldBe otherSampleOfferId
        result.orders[1].items[0].offer.price.shouldNotBeNull()
        result.orders[1].items[0].offer.price!! shouldBeEqualComparingTo "20.00".pln()
        result.orders[1].items[0].quantity shouldBe 5
        result.orders[1].items[0].totalPrice shouldBeEqualComparingTo "100.00".pln()
    }
})

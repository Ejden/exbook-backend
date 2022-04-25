package pl.exbook.exbook.features.shipping

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.ShippingDomainAbility
import pl.exbook.exbook.mock.offer
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.pln
import pl.exbook.exbook.shared.TestData.fourthSampleOfferId
import pl.exbook.exbook.shared.TestData.otherSampleOfferId
import pl.exbook.exbook.shared.TestData.otherSampleSellerId
import pl.exbook.exbook.shared.TestData.otherSampleShippingMethodId
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.shared.TestData.sampleSellerId
import pl.exbook.exbook.shared.TestData.sampleShippingMethodId
import pl.exbook.exbook.shared.TestData.thirdSampleOfferId
import pl.exbook.exbook.shipping.domain.PreviewAvailableShippingCommand
import pl.exbook.exbook.shipping.domain.PreviewAvailableShippingCommand.Order
import pl.exbook.exbook.shipping.domain.PreviewAvailableShippingCommand.OrderKey

class PreviewShippingSpec : ShouldSpec({
    val domain = ShippingDomainAbility()

    should("return one common shipping method for one order and one offer") {
        // given
        domain.thereIsShippingMethod {
            id = sampleShippingMethodId
        }
        val offer = offer {
            shippingMethod {
                id = sampleShippingMethodId
                price = "12.99".pln()
            }
        }
        domain.thereIsOffer(offer)
        val command = PreviewAvailableShippingCommand(
            orders = mapOf(
                OrderKey(sampleSellerId, OrderType.BUY) to Order(listOf(offer))
            )
        )

        // when
        val result = domain.facade.previewAvailableShipping(command)

        // then
        result.shippingByOrders shouldHaveSize 1

        // and
        val order = result.shippingByOrders.entries.first()
        order.key.sellerId shouldBe sampleSellerId
        order.key.orderType shouldBe OrderType.BUY
        order.value shouldHaveSize 1
        order.value[0].methodId shouldBe sampleShippingMethodId
        order.value[0].price shouldBeEqualComparingTo "12.99".pln()
    }

    should("return one common shipping method for one order with two offers") {
        // given
        domain.thereIsShippingMethod {
            id = sampleShippingMethodId
        }
        domain.thereIsShippingMethod {
            id = otherSampleShippingMethodId
        }
        val offer1 = offer {
            id = sampleOfferId
            shippingMethod {
                id = sampleShippingMethodId
                price = "12.99".pln()
            }
        }
        val offer2 = offer {
            id = otherSampleOfferId
            shippingMethod {
                id = sampleShippingMethodId
                price = "13.99".pln()
            }
            shippingMethod {
                id = otherSampleShippingMethodId
                price = "14.99".pln()
            }
        }
        domain.thereAreOffers(offer1, offer2)
        val command = PreviewAvailableShippingCommand(
            orders = mapOf(
                OrderKey(sampleSellerId, OrderType.BUY) to Order(listOf(offer1, offer2))
            )
        )

        // when
        val result = domain.facade.previewAvailableShipping(command)

        // then
        result.shippingByOrders shouldHaveSize 1

        // and
        val order = result.shippingByOrders.entries.first()
        order.key.sellerId shouldBe sampleSellerId
        order.key.orderType shouldBe OrderType.BUY
        order.value shouldHaveSize 1
        order.value[0].methodId shouldBe sampleShippingMethodId
        order.value[0].price shouldBeEqualComparingTo "13.99".pln()
    }

    should("return two common shipping methods for one order with two offers") {
        // given
        domain.thereIsShippingMethod {
            id = sampleShippingMethodId
        }
        domain.thereIsShippingMethod {
            id = otherSampleShippingMethodId
        }
        val offer1 = offer {
            id = sampleOfferId
            shippingMethod {
                id = sampleShippingMethodId
                price = "12.99".pln()
            }
            shippingMethod {
                id = otherSampleShippingMethodId
                price = "15.00".pln()
            }
        }
        val offer2 = offer {
            id = otherSampleOfferId
            shippingMethod {
                id = sampleShippingMethodId
                price = "13.99".pln()
            }
            shippingMethod {
                id = otherSampleShippingMethodId
                price = "14.99".pln()
            }
        }
        domain.thereAreOffers(offer1, offer2)
        val command = PreviewAvailableShippingCommand(
            orders = mapOf(
                OrderKey(sampleSellerId, OrderType.BUY) to Order(listOf(offer1, offer2))
            )
        )

        // when
        val result = domain.facade.previewAvailableShipping(command)

        // then
        result.shippingByOrders shouldHaveSize 1

        // and
        val order = result.shippingByOrders.entries.first()
        order.key.sellerId shouldBe sampleSellerId
        order.key.orderType shouldBe OrderType.BUY
        order.value shouldHaveSize 2
        order.value[0].methodId shouldBe sampleShippingMethodId
        order.value[0].price shouldBeEqualComparingTo "13.99".pln()
        order.value[1].methodId shouldBe otherSampleShippingMethodId
        order.value[1].price shouldBeEqualComparingTo "15.00".pln()
    }

    should("return no common shipping methods for one order with two offers") {
        // given
        domain.thereIsShippingMethod {
            id = sampleShippingMethodId
        }
        domain.thereIsShippingMethod {
            id = otherSampleShippingMethodId
        }
        val offer1 = offer {
            id = sampleOfferId
            shippingMethod {
                id = otherSampleShippingMethodId
                price = "15.00".pln()
            }
        }
        val offer2 = offer {
            id = otherSampleOfferId
            shippingMethod {
                id = sampleShippingMethodId
                price = "13.99".pln()
            }
        }
        domain.thereAreOffers(offer1, offer2)
        val command = PreviewAvailableShippingCommand(
            orders = mapOf(
                OrderKey(sampleSellerId, OrderType.BUY) to Order(listOf(offer1, offer2))
            )
        )

        // when
        val result = domain.facade.previewAvailableShipping(command)

        // then
        result.shippingByOrders shouldHaveSize 1

        // and
        val order = result.shippingByOrders.entries.first()
        order.key.sellerId shouldBe sampleSellerId
        order.key.orderType shouldBe OrderType.BUY
        order.value.shouldBeEmpty()
    }

    should("return common shipping methods for more than one order") {
        // given
        domain.thereIsShippingMethod {
            id = sampleShippingMethodId
        }
        domain.thereIsShippingMethod {
            id = otherSampleShippingMethodId
        }
        val offer1 = offer {
            id = sampleOfferId
            sellerId = sampleSellerId
            shippingMethod {
                id = sampleShippingMethodId
                price = "12.99".pln()
            }
            shippingMethod {
                id = otherSampleShippingMethodId
                price = "15.00".pln()
            }
        }
        val offer2 = offer {
            id = otherSampleOfferId
            sellerId = sampleSellerId
            shippingMethod {
                id = sampleShippingMethodId
                price = "13.99".pln()
            }
            shippingMethod {
                id = otherSampleShippingMethodId
                price = "14.99".pln()
            }
        }
        val offer3 = offer {
            id = thirdSampleOfferId
            sellerId = otherSampleSellerId
            shippingMethod {
                id = otherSampleShippingMethodId
                price = "14.99".pln()
            }
        }
        val offer4 = offer {
            id = fourthSampleOfferId
            sellerId = otherSampleSellerId
            shippingMethod {
                id = otherSampleShippingMethodId
                price = "20.99".pln()
            }
        }
        domain.thereAreOffers(offer1, offer2, offer3, offer4)
        val command = PreviewAvailableShippingCommand(
            orders = mapOf(
                OrderKey(sampleSellerId, OrderType.BUY) to Order(listOf(offer1, offer2)),
                OrderKey(otherSampleSellerId, OrderType.EXCHANGE) to Order(listOf(offer3, offer4))
            )
        )

        // when
        val result = domain.facade.previewAvailableShipping(command)

        // then
        result.shippingByOrders shouldHaveSize 2

        // and
        val order = result.shippingByOrders.entries.first()
        order.key.sellerId shouldBe sampleSellerId
        order.key.orderType shouldBe OrderType.BUY
        order.value shouldHaveSize 2
        order.value[0].methodId shouldBe sampleShippingMethodId
        order.value[0].price shouldBeEqualComparingTo "13.99".pln()
        order.value[1].methodId shouldBe otherSampleShippingMethodId
        order.value[1].price shouldBeEqualComparingTo "15.00".pln()

        // and
        val order2 = result.shippingByOrders.entries.toList()[1]
        order2.key.sellerId shouldBe otherSampleSellerId
        order2.key.orderType shouldBe OrderType.EXCHANGE
        order2.value shouldHaveSize 1
        order2.value[0].methodId shouldBe otherSampleShippingMethodId
        order2.value[0].price shouldBeEqualComparingTo "20.99".pln()
    }
})

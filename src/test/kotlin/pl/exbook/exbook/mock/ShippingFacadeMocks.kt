package pl.exbook.exbook.mock

import io.mockk.every
import io.mockk.slot
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.ShippingId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.TestData.sampleAddress
import pl.exbook.exbook.shared.TestData.sampleBuyerEmail
import pl.exbook.exbook.shared.TestData.sampleBuyerFirstName
import pl.exbook.exbook.shared.TestData.sampleBuyerLastName
import pl.exbook.exbook.shared.TestData.sampleBuyerPhoneNumber
import pl.exbook.exbook.shared.TestData.sampleCity
import pl.exbook.exbook.shared.TestData.sampleCountry
import pl.exbook.exbook.shared.TestData.samplePickupPointId
import pl.exbook.exbook.shared.TestData.samplePostalCost
import pl.exbook.exbook.shared.TestData.sampleShippingId
import pl.exbook.exbook.shared.TestData.sampleShippingMethodId
import pl.exbook.exbook.shared.TestData.sampleShippingMethodName
import pl.exbook.exbook.shipping.CalculateSelectedShippingCommand
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.shipping.domain.AddressShipping
import pl.exbook.exbook.shipping.domain.PickupPointShipping
import pl.exbook.exbook.shipping.domain.Shipping
import java.util.UUID

class ShippingFacadeMocks(private val shippingFacade: ShippingFacade) {
    fun willCreateShipping(idStrategy: IdGenerationStrategy, init: ShippingBuilder.() -> Unit) {
        val mockShipping = ShippingBuilder().apply(init).build(idStrategy)
        val command = slot<CalculateSelectedShippingCommand>()
        every { shippingFacade.calculateSelectedShipping(command = capture(command)) } answers {
            val capturedCommand = command.captured
            var answer: Shipping
            if (capturedCommand.pickupPoint != null) {
                answer = PickupPointShipping(
                    id = ShippingId(UUID.randomUUID().toString()),
                    shippingMethodId = capturedCommand.shippingMethodId,
                    shippingMethodName = mockShipping.shippingMethodName,
                    cost = mockShipping.cost,
                    pickupPoint = Shipping.PickupPoint(
                        firstAndLastName = capturedCommand.pickupPoint!!.firstAndLastName,
                        phoneNumber = capturedCommand.pickupPoint!!.phoneNumber,
                        email = capturedCommand.pickupPoint!!.email,
                        pickupPointId = capturedCommand.pickupPoint!!.pickupPointId
                    )
                )
            } else {
                answer = AddressShipping(
                    id = ShippingId(UUID.randomUUID().toString()),
                    shippingMethodId = capturedCommand.shippingMethodId,
                    shippingMethodName = mockShipping.shippingMethodName,
                    cost = mockShipping.cost,
                    address = Shipping.ShippingAddress(
                        firstAndLastName = capturedCommand.shippingAddress!!.firstAndLastName,
                        phoneNumber = capturedCommand.shippingAddress!!.phoneNumber,
                        email = capturedCommand.shippingAddress!!.email,
                        address = capturedCommand.shippingAddress!!.address,
                        postalCode = capturedCommand.shippingAddress!!.postalCode,
                        city = capturedCommand.shippingAddress!!.city,
                        country = capturedCommand.shippingAddress!!.country
                    )
                )
            }
            println(answer)
            answer
        }
    }
}

class ShippingBuilder {
    var id: ShippingId = sampleShippingId
    var shippingMethodId: ShippingMethodId = sampleShippingMethodId
    var shippingMethodName: String = sampleShippingMethodName
    var finalCost: Money = Money.zeroPln()
    var address: Shipping.ShippingAddress? = null
    var pickupPoint: Shipping.PickupPoint? = null

    fun build(idStrategy: IdGenerationStrategy): Shipping {
        id = if (idStrategy == IdGenerationStrategy.RANDOM) ShippingId(UUID.randomUUID().toString()) else id

        if (address != null) {
            return AddressShipping(
                id = id,
                shippingMethodId = shippingMethodId,
                shippingMethodName = shippingMethodName,
                cost = Shipping.Cost(finalCost),
                address = address!!
            )
        }

        if (pickupPoint != null) {
            return PickupPointShipping(
                id = id,
                shippingMethodId = shippingMethodId,
                shippingMethodName = shippingMethodName,
                cost = Shipping.Cost(finalCost),
                pickupPoint = pickupPoint!!
            )
        }

        throw IllegalArgumentException("Should provide at least one shipping address")
    }

    fun address(init: ShippingAddressBuilder.() -> Unit) {
        address = ShippingAddressBuilder().apply(init).build()
    }

    fun pickupPoint(init: PickupPointBuilder.() -> Unit) {
        pickupPoint = PickupPointBuilder().apply(init).build()
    }

    class ShippingAddressBuilder {
        var firstAndLastName: String = sampleBuyerFirstName + sampleBuyerLastName
        var phoneNumber: String = sampleBuyerPhoneNumber
        var email: String = sampleBuyerEmail
        var address: String = sampleAddress
        var postalCode: String = samplePostalCost
        var city: String = sampleCity
        var country: String = sampleCountry

        fun build() = Shipping.ShippingAddress(firstAndLastName, phoneNumber, email, address, postalCode, city, country)
    }

    class PickupPointBuilder {
        var firstAndLastName: String = sampleBuyerFirstName + sampleBuyerLastName
        var phoneNumber: String = sampleBuyerPhoneNumber
        var email: String = sampleBuyerEmail
        var pickupPointId: PickupPointId = samplePickupPointId

        fun build() = Shipping.PickupPoint(firstAndLastName, phoneNumber, email, pickupPointId)
    }
}

enum class IdGenerationStrategy {
    RANDOM,
    DEFINED
}

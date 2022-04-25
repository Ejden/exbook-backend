package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.shared.ShippingId
import pl.exbook.exbook.shipping.CalculateSelectedShippingCommand
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import java.util.UUID
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodType

class ShippingFactory {
    fun createShipping(
        selectedShippingMethod: ShippingMethod,
        request: CalculateSelectedShippingCommand,
        cost: Shipping.Cost
    ): Shipping = when(selectedShippingMethod.type) {
        ShippingMethodType.PICKUP_DELIVERY -> createShippingWithPickupPoint(selectedShippingMethod, request, cost)
        ShippingMethodType.ADDRESS_DELIVERY -> createShippingOnAddress(selectedShippingMethod, request, cost)
        ShippingMethodType.PERSONAL_DELIVERY -> createShippingWithPersonalDelivery(selectedShippingMethod, request, cost)
    }

    private fun createShippingWithPickupPoint(
        selectedShippingMethod: ShippingMethod,
        request: CalculateSelectedShippingCommand,
        cost: Shipping.Cost
    ) = PickupPointShipping(
        id = ShippingId(UUID.randomUUID().toString()),
        cost = cost,
        shippingMethodId = selectedShippingMethod.id,
        shippingMethodName = selectedShippingMethod.methodName,
        pickupPoint = request.pickupPoint!!.let {
            Shipping.PickupPoint(
                firstAndLastName = it.firstAndLastName,
                phoneNumber = it.phoneNumber,
                email = it.email,
                pickupPointId = it.pickupPointId
            )
        }
    )

    private fun createShippingOnAddress(
        selectedShippingMethod: ShippingMethod,
        request: CalculateSelectedShippingCommand,
        cost: Shipping.Cost
    ) = AddressShipping(
        id = ShippingId(UUID.randomUUID().toString()),
        cost = cost,
        shippingMethodId = selectedShippingMethod.id,
        shippingMethodName = selectedShippingMethod.methodName,
        address = request.shippingAddress!!.let {
            Shipping.ShippingAddress(
                firstAndLastName = it.firstAndLastName,
                phoneNumber = it.phoneNumber,
                email = it.email,
                address = it.address,
                postalCode = it.postalCode,
                city = it.city,
                country = it.country
            )
        }
    )

    private fun createShippingWithPersonalDelivery(
        selectedShippingMethod: ShippingMethod,
        request: CalculateSelectedShippingCommand,
        cost: Shipping.Cost
    ) = PersonalShipping(
        id = ShippingId(UUID.randomUUID().toString()),
        cost = cost,
        shippingMethodId = selectedShippingMethod.id,
        shippingMethodName = selectedShippingMethod.methodName
    )
}

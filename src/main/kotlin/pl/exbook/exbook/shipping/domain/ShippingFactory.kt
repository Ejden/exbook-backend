package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.shared.ShippingId
import pl.exbook.exbook.shipping.CalculateSelectedShippingRequest
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import java.util.UUID

class ShippingFactory {

    fun createShipping(selectedShippingMethod: ShippingMethod, request: CalculateSelectedShippingRequest): Shipping {
        return when {
            selectedShippingMethod.pickupPointMethod -> createShippingWithPickupPoint(selectedShippingMethod, request)
            else -> createShippingOnAddress(selectedShippingMethod, request)
        }
    }

    private fun createShippingWithPickupPoint(
        selectedShippingMethod: ShippingMethod,
        request: CalculateSelectedShippingRequest
    ) = PickupPointShipping(
        id = ShippingId(UUID.randomUUID().toString()),
        shippingMethodId = selectedShippingMethod.id,
        pickupPoint = request.pickupPoint!!.let { Shipping.PickupPoint(
            firstAndLastName = it.firstAndLastName,
            phoneNumber = it.phoneNumber,
            email = it.email,
            pickupPointId = it.pickupPointId
        ) }
    )

    private fun createShippingOnAddress(
        selectedShippingMethod: ShippingMethod,
        request: CalculateSelectedShippingRequest
    ) = AddressShipping(
        id = ShippingId(UUID.randomUUID().toString()),
        shippingMethodId = selectedShippingMethod.id,
        address = request.shippingAddress!!.let { Shipping.ShippingAddress(
            firstAndLastName = it.firstAndLastName,
            phoneNumber = it.phoneNumber,
            email = it.email,
            address = it.address,
            postalCode = it.postalCode,
            city = it.city,
            country = it.country
        ) }
    )
}

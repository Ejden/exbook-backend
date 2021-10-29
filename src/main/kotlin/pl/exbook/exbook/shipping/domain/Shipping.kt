package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.ShippingId
import pl.exbook.exbook.shared.ShippingMethodId

abstract class Shipping(
    val id: ShippingId,
    val shippingMethodId: ShippingMethodId
) {
    data class ShippingAddress(
        val firstAndLastName: String,
        val phoneNumber: String,
        val email: String,
        val address: String,
        val postalCode: String,
        val city: String,
        val country: String
    )

    data class PickupPoint(
        val firstAndLastName: String,
        val phoneNumber: String,
        val email: String,
        val pickupPointId: PickupPointId
    )
}

class PickupPointShipping(
    id: ShippingId,
    shippingMethodId: ShippingMethodId,
    val pickupPoint: PickupPoint
) : Shipping(id, shippingMethodId)

class AddressShipping(
    id: ShippingId,
    shippingMethodId: ShippingMethodId,
    val address: Shipping.ShippingAddress
) : Shipping(id, shippingMethodId)
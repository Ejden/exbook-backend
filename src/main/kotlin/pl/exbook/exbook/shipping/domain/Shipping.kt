package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.ShippingId
import pl.exbook.exbook.shared.ShippingMethodId

abstract class Shipping(
    val id: ShippingId,
    val shippingMethodId: ShippingMethodId,
    val shippingMethodName: String,
    val cost: Cost
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

    data class Cost(val finalCost: Money)
}

class PickupPointShipping(
    id: ShippingId,
    shippingMethodId: ShippingMethodId,
    shippingMethodName: String,
    cost: Cost,
    val pickupPoint: PickupPoint
) : Shipping(id, shippingMethodId, shippingMethodName, cost)

class AddressShipping(
    id: ShippingId,
    shippingMethodId: ShippingMethodId,
    shippingMethodName: String,
    cost: Cost,
    val address: ShippingAddress
) : Shipping(id, shippingMethodId, shippingMethodName, cost)

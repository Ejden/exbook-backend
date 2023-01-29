package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.ShippingId
import pl.exbook.exbook.shared.ShippingMethodId

abstract class Shipping(
    val id: ShippingId,
    val shippingMethodId: ShippingMethodId,
    val shippingMethodName: String,
    val sellerShippingInfo: SellerShippingInfo?,
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

    data class SellerShippingInfo(
        val address: ShippingAddress?,
        val pickupPoint: PickupPoint?
    )

    fun setSellerShippingInfo(shippingInfo: SellerShippingInfo): Shipping = when (this) {
        is PickupPointShipping -> PickupPointShipping(id, shippingMethodId, shippingMethodName, shippingInfo, cost, pickupPoint)
        is AddressShipping -> AddressShipping(id, shippingMethodId, shippingMethodName, shippingInfo, cost, address)
        is PersonalShipping -> PersonalShipping(id, shippingMethodId, shippingMethodName, shippingInfo, cost)
        else -> throw Exception("Cannot create shipping. Unknown type")
    }
}

class PickupPointShipping(
    id: ShippingId,
    shippingMethodId: ShippingMethodId,
    shippingMethodName: String,
    sellerShippingInfo: SellerShippingInfo? = null,
    cost: Cost,
    val pickupPoint: PickupPoint
) : Shipping(id, shippingMethodId, shippingMethodName, sellerShippingInfo, cost)

class AddressShipping(
    id: ShippingId,
    shippingMethodId: ShippingMethodId,
    shippingMethodName: String,
    sellerShippingInfo: SellerShippingInfo? = null,
    cost: Cost,
    val address: ShippingAddress
) : Shipping(id, shippingMethodId, shippingMethodName, sellerShippingInfo, cost)

class PersonalShipping(
    id: ShippingId,
    shippingMethodId: ShippingMethodId,
    shippingMethodName: String,
    sellerShippingInfo: SellerShippingInfo? = null,
    cost: Cost
) : Shipping(id, shippingMethodId, shippingMethodName, sellerShippingInfo, cost)

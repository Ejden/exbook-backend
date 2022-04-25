package pl.exbook.exbook.baskettransaction.domain

import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.UserId

data class PreviewPurchaseCommand(
    val orders: List<Order>
) {
    data class Order(
        val sellerId: UserId,
        val orderType: OrderType,
        val shipping: Shipping?
    )

    data class Shipping(
        val shippingMethodId: ShippingMethodId,
        val shippingAddress: ShippingAddress?,
        val pickupPoint: PickupPoint?
    )

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

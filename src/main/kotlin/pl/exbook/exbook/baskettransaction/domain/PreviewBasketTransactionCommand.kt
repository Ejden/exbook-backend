package pl.exbook.exbook.baskettransaction.domain

import java.time.Instant
import pl.exbook.exbook.basket.domain.Basket
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.domain.User

data class PreviewBasketTransactionCommand(
    val buyer: User,
    val basket: Basket,
    val offers: List<Offer>,
    val shipping: List<Shipping>,
    val timestamp: Instant
) {
    data class Shipping(
        val sellerId: UserId,
        val orderType: Order.OrderType,
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

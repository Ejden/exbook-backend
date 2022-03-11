package pl.exbook.exbook.baskettransaction.domain

import java.time.Instant
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.PurchaseId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.UserId

data class DraftPurchase(
    val purchaseId: PurchaseId,
    val buyer: Buyer,
    val orders: List<DraftOrder>,
    val creationDate: Instant,
    val lastUpdated: Instant
) {
    data class DraftOrder(
        val orderId: OrderId,
        val orderType: Order.OrderType,
        val seller: Seller,
        val items: List<Item>,
        val shipping: Shipping?
    )

    data class Buyer(
        val id: UserId
    )

    data class Seller(
        val id: UserId
    )

    data class Item(
        val offerId: OfferId,
        val quantity: Int,
        val price: Money
    )

    data class Shipping(
        val shippingMethodId: ShippingMethodId,
        val pickupPoint: PickupPoint?,
        val shippingAddress: ShippingAddress?
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

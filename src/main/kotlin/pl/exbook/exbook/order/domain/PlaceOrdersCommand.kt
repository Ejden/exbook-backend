package pl.exbook.exbook.order.domain

import java.time.Instant
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.ExchangeBookId
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.PurchaseId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodType
import pl.exbook.exbook.user.domain.User

data class PlaceOrdersCommand(
    val purchaseId: PurchaseId,
    val buyer: User,
    val orders: List<Order>,
    val timestamp: Instant
) {
    data class Order(
        val orderId: OrderId,
        val items: List<Item>,
        val seller: Seller,
        val shipping: Shipping,
        val exchangeBooks: List<Book>,
        val orderType: OrderType,
        val note: String
    )

    data class Item(
        val offerId: OfferId,
        val quantity: Long
    )

    data class Seller(
        val id: UserId
    )

    data class Shipping(
        val shippingMethodId: ShippingMethodId,
        val shippingMethodName: String,
        val shippingMethodType: ShippingMethodType,
        val shippingAddress: ShippingAddress?,
        val pickupPoint: PickupPoint?,
        val cost: ShippingCost
    )

    data class ShippingCost(
        val finalCost: Money
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

    data class Book(
        val id: ExchangeBookId,
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: Offer.Condition,
        val quantity: Int
    )
}

package pl.exbook.exbook.order.domain

import java.time.Instant
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.ShippingId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodType

data class OrderSnippet(
    val id: OrderId,
    val buyer: Buyer,
    val seller: Seller,
    val shipping: Shipping,
    val sellerShippingInfo: SellerShippingInfo?,
    val items: List<OrderItem>,
    val orderType: Order.OrderType,
    val exchangeBooks: List<Order.ExchangeBook>,
    val orderDate: Instant,
    val status: Order.OrderStatus,
    val totalCost: Money,
    val note: String,
    val availableActions: Actions
) {
    data class Buyer(
        val id: UserId,
        val name: String,
        val firstName: String,
        val lastName: String
    )

    data class Seller(
        val id: UserId,
        val name: String,
        val firstName: String,
        val lastName: String
    )

    data class Shipping(
        val id: ShippingId,
        val methodName: String,
        val methodType: ShippingMethodType,
        val shippingAddress: ShippingAddress?,
        val pickupPoint: PickupPoint?,
        val cost: Cost
    )

    data class SellerShippingInfo(
        val address: ShippingAddress?,
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

    data class OrderItem(
        val offerId: OfferId,
        val book: Book,
        val images: Images,
        val quantity: Long,
        val cost: Money?
    )

    data class Book(
        val author: String,
        val title: String
    )

    data class Images(
        val thumbnail: Image?
    )

    data class Image(val url: String)

    data class Cost(val finalCost: Money)

    data class Actions(
        val buyerActions: BuyerActions,
        val sellerActions: SellerActions
    )

    data class BuyerActions(
        val canBeReturned: Boolean,
        val canBeCancelled: Boolean,
        val canBeMarkedAsDelivered: Boolean
    )

    data class SellerActions(
        val canBeCancelled: Boolean,
        val canExchangeBeDismissed: Boolean,
        val canExchangeBeAccepted: Boolean,
        val canBeMarkedAsSent: Boolean,
        val canBeMarkedAsReturnDelivered: Boolean
    )
}

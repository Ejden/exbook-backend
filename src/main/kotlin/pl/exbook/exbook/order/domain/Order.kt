package pl.exbook.exbook.order.domain

import java.time.Duration
import pl.exbook.exbook.offer.domain.Offer
import java.time.Instant
import pl.exbook.exbook.shared.ExchangeBookId
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.ShippingId
import pl.exbook.exbook.shared.UserId

data class Order(
    val id: OrderId,
    val buyer: Buyer,
    val seller: Seller,
    val shipping: Shipping,
    val items: List<OrderItem>,
    val orderType: OrderType,
    val exchangeBooks: List<ExchangeBook>,
    val orderDate: Instant,
    val status: OrderStatus,
    val totalCost: Money,
    val note: String
) {
    val canBeReturned: Boolean
        get() = Duration.between(orderDate, Instant.now()).abs().toDays() <= 14
    val canBeCancelled: Boolean
        get() = status == OrderStatus.WAITING_FOR_ACCEPT || status == OrderStatus.NEW || status == OrderStatus.ACCEPTED
    val canBeMarkedAsDelivered: Boolean
        get() = status == OrderStatus.ACCEPTED || status == OrderStatus.NEW || status == OrderStatus.SENT
    val canExchangeBeDismissed: Boolean
        get() = status == OrderStatus.WAITING_FOR_ACCEPT
    val canExchangeBeAccepted: Boolean
        get() = status == OrderStatus.WAITING_FOR_ACCEPT
    val canBeMarkedAsSent: Boolean
        get() = status == OrderStatus.NEW || status == OrderStatus.ACCEPTED

    data class OrderItem(
        val offerId: OfferId,
        val quantity: Long,
        val cost: Money?
    )

    data class Buyer(val id: UserId)

    data class Seller(val id: UserId)

    data class Shipping(val id: ShippingId)

    data class ExchangeBook(
        val id: ExchangeBookId,
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: Offer.Condition,
        val quantity: Int
    )

    enum class OrderType {
        EXCHANGE,
        BUY
    }

    enum class OrderStatus {
        NEW,
        WAITING_FOR_ACCEPT,
        SENT,
        DELIVERED,
        DECLINED,
        ACCEPTED,
        RETURNED,
        CANCELED
    }
}

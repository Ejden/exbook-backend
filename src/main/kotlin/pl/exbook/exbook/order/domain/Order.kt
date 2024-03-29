package pl.exbook.exbook.order.domain

import java.time.Duration
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.*
import java.time.Instant

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
                && status != OrderStatus.RETURN_IN_PROGRESS
                && status != OrderStatus.RETURN_DELIVERED
                && status != OrderStatus.CANCELED
                && status != OrderStatus.DECLINED
    val canBeMarkedAsReturnDelivered: Boolean
        get() = status == OrderStatus.RETURN_DELIVERED
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

    fun changeStatus(newStatus: OrderStatus) = this.copy(status = newStatus)

    data class OrderItem(
        val offerId: OfferId,
        val quantity: Long,
        val cost: Money?
    )

    data class Buyer(val id: UserId)

    data class Seller(val id: UserId)

    data class Shipping(
        val id: ShippingId,
    )

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
        RETURN_DELIVERED,
        RETURN_IN_PROGRESS,
        CANCELED
    }
}

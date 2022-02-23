package pl.exbook.exbook.order.domain

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
    data class OrderItem(
        val offerId: OfferId,
        val quantity: Int,
        val cost: Money?
    )

    data class Buyer(val id: UserId)

    data class Seller(val id: UserId)

    data class Shipping(val id: ShippingId)

    data class ExchangeBook(
        val author: String,
        val title: String,
        val isbn: Long?,
        val condition: Offer.Condition
    )

    enum class OrderType {
        EXCHANGE,
        BUY
    }

    enum class OrderStatus {
        NEW,
        WAITING_FOR_ACCEPT,
        DECLINED,
        ACCEPTED,
        RETURNED
    }
}

package pl.exbook.exbook.order.domain

import java.time.Instant
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.ShippingId
import pl.exbook.exbook.shared.UserId

data class OrderSnippet(
    val id: OrderId,
    val buyer: Buyer,
    val seller: Seller,
    val shipping: Shipping,
    val items: List<OrderItem>,
    val orderType: Order.OrderType,
    val exchangeBooks: List<Order.ExchangeBook>,
    val orderDate: Instant,
    val status: Order.OrderStatus,
    val totalCost: Money,
    val note: String
) {
    data class Buyer(val id: UserId)

    data class Seller(
        val id: UserId,
        val name: String,
        val firstName: String,
        val lastName: String
    )

    data class Shipping(
        val id: ShippingId,
        val methodName: String,
        val cost: Cost
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
}

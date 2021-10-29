package pl.exbook.exbook.order.domain

import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.UserId
import java.time.Instant

data class Order(
    val id: OrderId?,
    val buyer: Buyer,
    val shippingId: ShippingId,
    val items: List<OrderItem>,
    val orderDate: Instant,
    val returned: Boolean,
    val accepted: Boolean
) {
    data class OrderItem(
        val offerId: OfferId,
        val seller: Seller,
        val orderType: OrderType,
        val exchangeBook: ExchangeBook?,
        val quantity: Int,
        val price: Money?
    )

    data class Buyer(val id: UserId)

    data class Seller(val id: UserId)

    data class ExchangeBook(
        val author: String,
        val title: String,
        val isbn: Long?,
        val condition: Offer.Condition
    )

    data class ShippingId(val raw: String)

    enum class OrderType {
        EXCHANGE,
        BUY
    }
}

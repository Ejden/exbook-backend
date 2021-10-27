package pl.exbook.exbook.order.domain

import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.UserId
import java.time.Instant

class Order(
    val id: OrderId?,
    val buyer: Buyer,
    val items: List<OrderItem>,
    val orderDate: Instant,
    val returned: Boolean
) {
    class OrderItem(
        val offerId: OfferId,
        val seller: Seller,
        val orderType: OrderType,
        val exchangeBook: ExchangeBook?,
        val quantity: Int,
        val price: Money?
    )

    class Buyer(
        val id: UserId
    )

    class Seller(
        val id: UserId
    )

    class ExchangeBook(
        val author: String,
        val title: String,
        val isbn: Long?,
        val condition: Offer.Condition
    )

    enum class OrderType {
        EXCHANGE,
        BUY
    }
}

package pl.exbook.exbook.order.domain

import pl.exbook.exbook.shared.Currency
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shipping.domain.Shipping
import java.time.Instant
import java.util.UUID
import pl.exbook.exbook.offer.domain.Offer

class OrderFactory {
    fun createOrder(
        command: PlaceOrdersCommand.Order,
        offers: List<Offer>,
        buyerId: UserId,
        shipping: Shipping
    ): Order = with(command) {
        val orderItems = command.items.map {
            val offer = offers.first { o -> o.id == it.offerId }
            it.toOrderItem(offer.price)
        }

        Order(
            id = OrderId(UUID.randomUUID().toString()),
            buyer = Order.Buyer(buyerId),
            seller = Order.Seller(command.seller.id),
            shipping = Order.Shipping(shipping.id),
            items = orderItems,
            orderType = command.orderType,
            exchangeBooks = command.exchangeBooks.map { it.toExchangeBook() },
            orderDate = Instant.now(),
            status = if (command.orderType == Order.OrderType.BUY) Order.OrderStatus.NEW else Order.OrderStatus.WAITING_FOR_ACCEPT,
            totalCost = Money.sum(orderItems.map { it.cost ?: Money.zero(Currency.PLN) } + shipping.cost.finalCost),
            note = command.note
        )
    }
}

private fun PlaceOrdersCommand.Item.toOrderItem(price: Money?) = Order.OrderItem(
    offerId = this.offerId,
    quantity = this.quantity,
    cost = price
)

private fun PlaceOrdersCommand.Book.toExchangeBook() = Order.ExchangeBook(
    id = this.id,
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition,
    quantity = quantity
)

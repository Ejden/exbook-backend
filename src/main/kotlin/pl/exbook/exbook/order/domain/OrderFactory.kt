package pl.exbook.exbook.order.domain

import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.adapter.rest.NewOrderDto
import pl.exbook.exbook.shared.Currency
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shipping.domain.Shipping
import pl.exbook.exbook.user.domain.User
import java.time.Instant
import java.util.*

class OrderFactory(
    private val offerFacade: OfferFacade
) {

    fun createOrder(newOrderRequest: NewOrderRequest): Order {
        return with(newOrderRequest) {
            val offers = newOrder.items.map { offerFacade.getOffer(OfferId(it.offerId)) }
            val orderItems = newOrder.items.map {
                val offer = offers.find { o -> o.id == OfferId(it.offerId) }!!
                it.toOrderItem(offer.price)
            }

            Order(
                id = OrderId(UUID.randomUUID().toString()),
                buyer = Order.Buyer(buyer.id!!),
                seller = Order.Seller(sellerId),
                shipping = Order.Shipping(shipping.id),
                items = orderItems,
                orderDate = Instant.now(),
                status = Order.OrderStatus.NEW,
                totalCost = Money.sum(orderItems.map { it.cost ?: Money.zero(Currency.PLN) } + shipping.cost.finalCost)
            )
        }
    }
}

data class NewOrderRequest(
    val newOrder: NewOrderDto,
    val sellerId: UserId,
    val buyer: User,
    val shipping: Shipping
)

private fun NewOrderDto.OrderItemDto.toOrderItem(cost: Money?) = Order.OrderItem(
    offerId = OfferId(this.offerId),
    orderType = Order.OrderType.valueOf(this.orderType),
    exchangeBook = this.exchangeBook?.toDomain(),
    quantity = this.quantity,
    cost = cost
)

private fun NewOrderDto.ExchangeBookDto.toDomain() = Order.ExchangeBook(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = Offer.Condition.valueOf(this.condition)
)

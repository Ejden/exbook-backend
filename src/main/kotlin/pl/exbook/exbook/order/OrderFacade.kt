package pl.exbook.exbook.order

import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.adapter.rest.NewOrderDto
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.order.domain.OrderRepository
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User
import java.time.Instant

class OrderFacade(
    private val orderRepository: OrderRepository,
    private val offerFacade: OfferFacade,
    private val userFacade: UserFacade
) {

    fun placeOrder(newOrder: NewOrderDto, buyerName: String): Order {
        val buyer: User = userFacade.getUserByUsername(buyerName)

        val order = Order(
            id = null,
            buyer = Order.Buyer(
                id = buyer.id!!
            ),
            items = newOrder.items.map {
                val offer = offerFacade.getOffer(OfferId(it.offerId))
                it.toOrderItem(offer.seller.id, offer.price)
            },
            orderDate = Instant.now(),
            returned = false
        )

        return orderRepository.save(order)
    }

    fun getOrder(orderId: OrderId) = orderRepository.findById(orderId) ?: OrderNotFoundException()
}

private fun NewOrderDto.OrderItemDto.toOrderItem(sellerId: UserId, price: Money?) = Order.OrderItem(
    offerId = OfferId(this.offerId),
    seller = Order.Seller(sellerId),
    orderType = Order.OrderType.valueOf(this.orderType),
    exchangeBook = this.exchangeBook?.toDomain(),
    quantity = this.quantity,
    price = price
)

private fun NewOrderDto.ExchangeBookDto.toDomain() = Order.ExchangeBook(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = Offer.Condition.valueOf(this.condition)
)

class OrderNotFoundException : RuntimeException()

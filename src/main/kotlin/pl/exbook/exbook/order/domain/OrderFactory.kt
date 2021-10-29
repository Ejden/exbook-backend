package pl.exbook.exbook.order.domain

import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.adapter.rest.NewOrderDto
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
            Order(
                id = OrderId(UUID.randomUUID().toString()),
                buyer = Order.Buyer(buyer.id!!),
                items = newOrder.items.map {
                    val offer = offerFacade.getOffer(OfferId(it.offerId))
                    it.toOrderItem(offer.seller.id, offer.price)
                },
                orderDate = Instant.now(),
                returned = false,
                accepted = false,
                shippingId = Order.ShippingId(shipping.id.raw)
            )
        }
    }
}

data class NewOrderRequest(
    val newOrder: NewOrderDto,
    val buyer: User,
    val shipping: Shipping
)

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

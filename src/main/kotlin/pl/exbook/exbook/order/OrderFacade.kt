package pl.exbook.exbook.order

import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.adapter.rest.NewOrderDto
import pl.exbook.exbook.order.domain.*
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shipping.CalculateSelectedShippingRequest
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User

class OrderFacade(
    private val orderRepository: OrderRepository,
    private val userFacade: UserFacade,
    private val shippingFacade: ShippingFacade,
    private val offerFacade: OfferFacade,
    private val orderValidator: OrderValidator,
    private val orderFactory: OrderFactory
) {

    fun placeOrder(newOrder: NewOrderDto, buyerName: String): Order {
        val buyer = userFacade.getUserByUsername(buyerName)
        val offers = newOrder.items.map { offerFacade.getOffer(OfferId(it.offerId)) }
        orderValidator.validate(newOrder, offers)
        val shipping = shippingFacade.calculateSelectedShipping(createShippingRequest(newOrder, buyer))
        val order = orderFactory.createOrder(NewOrderRequest(newOrder, buyer, shipping))

        shippingFacade.save(shipping)
        return orderRepository.save(order)
    }

    fun getOrder(orderId: OrderId) = orderRepository.findById(orderId) ?: OrderNotFoundException(orderId)

    private fun createShippingRequest(newOrder: NewOrderDto, buyer: User) = CalculateSelectedShippingRequest(
        shippingMethodId = ShippingMethodId(newOrder.shipping.shippingMethodId),
        orderItems = newOrder.items.map { CalculateSelectedShippingRequest.OrderItem(
            offerId = OfferId(it.offerId),
            orderType = Order.OrderType.valueOf(it.orderType),
            exchangeBook = it.exchangeBook?.let {
                    book -> CalculateSelectedShippingRequest.ExchangeBook(book.author, book.title, book.isbn, Offer.Condition.valueOf(book.condition))
            },
            quantity = it.quantity
        ) },
        shippingAddress = newOrder.shipping.shippingAddress?.let {
            CalculateSelectedShippingRequest.ShippingAddress(it.firstAndLastName, it.phoneNumber, it.email, it.address, it.postalCode, it.city, it.country)
        },
        pickupPoint = newOrder.shipping.pickupPoint?.let {
            CalculateSelectedShippingRequest.PickupPoint(it.firstAndLastName, it.phoneNumber, it.email, PickupPointId(it.pickupPointId))
        }
    )
}

class OrderNotFoundException(orderId: OrderId) : RuntimeException("Order with id $orderId not found")

package pl.exbook.exbook.order

import org.springframework.data.domain.Page
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.adapter.mongodb.OrderNotFoundException
import pl.exbook.exbook.order.adapter.rest.NewOrderDto
import pl.exbook.exbook.order.domain.NewOrderRequest
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.order.domain.OrderFactory
import pl.exbook.exbook.order.domain.OrderRepository
import pl.exbook.exbook.order.domain.OrderSnippet
import pl.exbook.exbook.order.domain.OrderValidator
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shipping.CalculateSelectedShippingRequest
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.shipping.domain.Shipping
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
        val shipping = shippingFacade.calculateSelectedShipping(createShippingRequest(newOrder, offers))
        val order = orderFactory.createOrder(NewOrderRequest(newOrder, UserId(newOrder.seller.id), buyer, shipping))

        shippingFacade.save(shipping)
        return orderRepository.save(order)
    }

    fun getOrder(orderId: OrderId, username: String): Order {
        val user = userFacade.getUserByUsername(username)
        val order = orderRepository.findById(orderId) ?: throw OrderNotFoundException(orderId)
        checkIfOrderBelongsToUser(user, order)
        return order
    }

    fun getUserOrders(username: String, itemsPerPage: Int?, page: Int?): Page<Order> {
        val userId = userFacade.getUserByUsername(username).id!!
        return orderRepository.findByBuyerId(userId, itemsPerPage, page, null)
    }

    fun getUserOrdersSnippets(username: String, itemsPerPage: Int?, page: Int?): Page<OrderSnippet> {
        val userId = userFacade.getUserByUsername(username).id!!
        return orderRepository.findBySellerId(userId, itemsPerPage, page, null).map {
            val seller = userFacade.getUserById(it.seller.id)
            val shipping = shippingFacade.findShipping(it.shipping.id)
            val items = it.items.map { item -> toOrderSnippetItem(item) }
            it.toOrderSnippet(seller, shipping, items)
        }
    }

    fun getSellerOrders(username: String, itemsPerPage: Int?, page: Int?): Page<Order> {
        val sellerId = userFacade.getUserByUsername(username).id!!
        return orderRepository.findBySellerId(sellerId, itemsPerPage, page, null)
    }

    private fun toOrderSnippetItem(item: Order.OrderItem): OrderSnippet.OrderItem {
        val offer = offerFacade.getOffer(item.offerId)
        return OrderSnippet.OrderItem(
            offerId = offer.id,
            book = OrderSnippet.Book(
                author = offer.book.author,
                title = offer.book.title
            ),
            orderType = item.orderType,
            images = OrderSnippet.Images(offer.images.thumbnail?.let { OrderSnippet.Image(it.url) }),
            quantity = item.quantity,
            cost = item.cost
        )
    }

    private fun createShippingRequest(newOrder: NewOrderDto, offers: List<Offer>) = CalculateSelectedShippingRequest(
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
        },
        offersShippingMethods = offers.associateBy { it.id }.mapValues { it.value.shippingMethods }
    )

    private fun checkIfOrderBelongsToUser(user: User, order: Order) {
        if (user.id!! != order.buyer.id) throw OrderNotFoundException(order.id!!)
    }
}

private fun Order.toOrderSnippet(seller: User, shipping: Shipping, orderItems: List<OrderSnippet.OrderItem>) = OrderSnippet(
    id = this.id!!,
    buyer = OrderSnippet.Buyer(this.buyer.id),
    seller = OrderSnippet.Seller(seller.id!!, seller.login, seller.firstName, seller.lastName),
    shipping = OrderSnippet.Shipping(this.shipping.id, shipping.shippingMethodName, OrderSnippet.Cost(shipping.cost.finalCost)),
    items = orderItems,
    orderDate = this.orderDate,
    status = this.status,
    totalCost = this.totalCost
)

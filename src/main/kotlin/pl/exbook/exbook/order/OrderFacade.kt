package pl.exbook.exbook.order

import mu.KLogging
import org.springframework.data.domain.Page
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.adapter.mongodb.OrderNotFoundException
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.order.domain.OrderFactory
import pl.exbook.exbook.order.domain.OrderRepository
import pl.exbook.exbook.order.domain.OrderSnippet
import pl.exbook.exbook.order.domain.OrderValidator
import pl.exbook.exbook.order.domain.PlaceOrdersCommand
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shipping.CalculateSelectedShippingCommand
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.shipping.domain.Shipping
import pl.exbook.exbook.stock.StockFacade
import pl.exbook.exbook.stock.domain.StockReservation
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User

class OrderFacade(
    private val orderRepository: OrderRepository,
    private val userFacade: UserFacade,
    private val shippingFacade: ShippingFacade,
    private val offerFacade: OfferFacade,
    private val orderValidator: OrderValidator,
    private val orderFactory: OrderFactory,
    private val stockFacade: StockFacade
) {
    fun placeOrders(command: PlaceOrdersCommand, buyerName: String): List<Order> {
        val buyer = userFacade.getUserByUsername(buyerName)
        val orders = mutableListOf<Order>()

        command.orders.forEach { order ->
            try {
                orders += placeOrder(order, buyer)
            } catch (cause: Exception) {
                logger.error { "Error creating order for buyer: $buyerName" }
            }
        }

        return orders
    }

    private fun placeOrder(command: PlaceOrdersCommand.Order, buyer: User): Order {
        val offers = command.items.map { offerFacade.getOffer(it.offerId) }
        val stockReservations = mutableListOf<StockReservation>()
        var order: Order? = null
        var shipping: Shipping? = null

        try {
            orderValidator.validate(command, offers)
            stockReservations += reserveOffers(command, offers)
            shipping = shippingFacade.calculateSelectedShipping(createShippingCommand(command, offers))
            order = orderFactory.createOrder(command, offers, buyer.id, shipping)

            shippingFacade.save(shipping)
            val savedOrder = orderRepository.save(order)
            confirmOffersReservations(stockReservations)

            return savedOrder
        } catch (cause: Exception) {
            stockReservations.forEach {
                try {
                    stockFacade.cancelReservation(it.reservationId)
                } catch (cause: Exception) {
                    logger.error(cause) { "Unable to cancel reservation ${it.reservationId.raw} after order creation error" }
                }
            }

            try {
                shipping?.let { shippingFacade.remove(it.id) }
            } catch (cause: Exception) {
                logger.error(cause) { "Unable to remove shipping ${shipping?.id} after order creation error" }
            }

            try {
                order?.let { orderRepository.remove(it.id) }
            } catch (cause: Exception) {
                logger.error(cause) { "Unable to remove order ${order?.id} after order creation error" }
            }

            throw cause
        }
    }

    private fun reserveOffers(
        order: PlaceOrdersCommand.Order,
        offers: List<Offer>
    ) : List<StockReservation> = order.items.map {
        val correspondingOffer = offers.first { offer -> offer.id == it.offerId }
        stockFacade.reserve(correspondingOffer.stockId, it.quantity)
    }

    private fun createShippingCommand(
        command: PlaceOrdersCommand.Order,
        offers: List<Offer>
    ) = CalculateSelectedShippingCommand(
        shippingMethodId = command.shipping.shippingMethodId,
        orderItems = command.items.map {
            CalculateSelectedShippingCommand.OrderItem(
                offerId = it.offerId,
                quantity = it.quantity
            )
        },
        shippingAddress = command.shipping.shippingAddress?.let {
            CalculateSelectedShippingCommand.ShippingAddress(
                firstAndLastName = it.firstAndLastName,
                phoneNumber = it.phoneNumber,
                email = it.email,
                address = it.address,
                postalCode = it.postalCode,
                city = it.city,
                country = it.country
            )
        },
        pickupPoint = command.shipping.pickupPoint?.let {
            CalculateSelectedShippingCommand.PickupPoint(
                firstAndLastName = it.firstAndLastName,
                phoneNumber = it.phoneNumber,
                email = it.email,
                pickupPointId = it.pickupPointId
            )
        },
        offersShippingMethods = offers.associateBy { it.id }.mapValues { it.value.shippingMethods }
    )

    fun getOrder(orderId: OrderId, username: String): Order {
        val user = userFacade.getUserByUsername(username)
        val order = orderRepository.findById(orderId) ?: throw OrderNotFoundException(orderId)
        checkIfOrderBelongsToUser(user, order)
        return order
    }

    fun getUserOrders(username: String, itemsPerPage: Int?, page: Int?): Page<Order> {
        val userId = userFacade.getUserByUsername(username).id
        return orderRepository.findByBuyerId(userId, itemsPerPage, page, null)
    }

    fun getUserOrdersSnippets(username: String, itemsPerPage: Int?, page: Int?): Page<OrderSnippet> {
        val userId = userFacade.getUserByUsername(username).id
        return orderRepository.findBySellerId(userId, itemsPerPage, page, null).map {
            val seller = userFacade.getUserById(it.seller.id)
            val shipping = shippingFacade.findShipping(it.shipping.id)
            val items = it.items.map { item -> toOrderSnippetItem(item) }
            it.toOrderSnippet(seller, shipping, items)
        }
    }

    fun getSellerOrders(username: String, itemsPerPage: Int?, page: Int?): Page<Order> {
        val sellerId = userFacade.getUserByUsername(username).id
        return orderRepository.findBySellerId(sellerId, itemsPerPage, page, null)
    }

    private fun confirmOffersReservations(stockReservations: List<StockReservation>) = stockReservations.forEach {
        stockFacade.confirmReservation(it.reservationId)
    }

    private fun toOrderSnippetItem(item: Order.OrderItem): OrderSnippet.OrderItem {
        val offer = offerFacade.getOffer(item.offerId)
        return OrderSnippet.OrderItem(
            offerId = offer.id,
            book = OrderSnippet.Book(
                author = offer.book.author,
                title = offer.book.title
            ),
            images = OrderSnippet.Images(offer.images.thumbnail?.let { OrderSnippet.Image(it.url) }),
            quantity = item.quantity,
            cost = item.cost
        )
    }

    private fun checkIfOrderBelongsToUser(user: User, order: Order) {
        if (user.id != order.buyer.id) throw OrderNotFoundException(order.id)
    }

    companion object : KLogging()
}

private fun Order.toOrderSnippet(seller: User, shipping: Shipping, orderItems: List<OrderSnippet.OrderItem>) = OrderSnippet(
    id = this.id,
    buyer = OrderSnippet.Buyer(this.buyer.id),
    seller = OrderSnippet.Seller(seller.id, seller.username, seller.firstName, seller.lastName),
    shipping = OrderSnippet.Shipping(this.shipping.id, shipping.shippingMethodName, OrderSnippet.Cost(shipping.cost.finalCost)),
    items = orderItems,
    orderType = this.orderType,
    exchangeBooks = this.exchangeBooks,
    orderDate = this.orderDate,
    status = this.status,
    totalCost = this.totalCost,
    note = this.note
)

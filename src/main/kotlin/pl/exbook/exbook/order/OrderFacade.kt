package pl.exbook.exbook.order

import java.time.Instant
import java.util.UUID
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
import pl.exbook.exbook.shared.ShippingId
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.shipping.domain.AddressShipping
import pl.exbook.exbook.shipping.domain.PersonalShipping
import pl.exbook.exbook.shipping.domain.PickupPointShipping
import pl.exbook.exbook.shipping.domain.Shipping
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodType
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
    fun placeOrders(command: PlaceOrdersCommand): List<Order> {
        val orders = mutableListOf<Order>()

        command.orders.forEach { order ->
            try {
                orders += placeOrder(order, command.buyer, command.timestamp)
            } catch (cause: Exception) {
                logger.error { "Error creating order ${order.orderId.raw} from purchase ${command.purchaseId.raw}" }
            }
        }

        return orders
    }

    private fun placeOrder(command: PlaceOrdersCommand.Order, buyer: User, timestamp: Instant): Order {
        val offers = command.items.map { offerFacade.getOfferVersion(it.offerId, timestamp) }
        val stockReservations = mutableListOf<StockReservation>()
        var order: Order? = null
        var shipping: Shipping? = null

        try {
            orderValidator.validate(command, offers)
            stockReservations += reserveOffers(command, offers)
            shipping = createShippingFromDraft(command.shipping)
            order = orderFactory.createOrder(command, offers, buyer.id, shipping)

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

    private fun createShippingFromDraft(shipping: PlaceOrdersCommand.Shipping): Shipping = when (shipping.shippingMethodType) {
        ShippingMethodType.PICKUP_DELIVERY -> PickupPointShipping(
            id = ShippingId(UUID.randomUUID().toString()),
            shippingMethodId = shipping.shippingMethodId,
            shippingMethodName = shipping.shippingMethodName,
            cost = Shipping.Cost(shipping.cost.finalCost),
            pickupPoint = Shipping.PickupPoint(
                firstAndLastName = shipping.pickupPoint!!.firstAndLastName,
                phoneNumber = shipping.pickupPoint.phoneNumber,
                email = shipping.pickupPoint.email,
                pickupPointId = shipping.pickupPoint.pickupPointId
            )
        )
        ShippingMethodType.ADDRESS_DELIVERY -> AddressShipping(
            id = ShippingId(UUID.randomUUID().toString()),
            shippingMethodId = shipping.shippingMethodId,
            shippingMethodName = shipping.shippingMethodName,
            cost = Shipping.Cost(shipping.cost.finalCost),
            address = Shipping.ShippingAddress(
                firstAndLastName = shipping.shippingAddress!!.firstAndLastName,
                phoneNumber = shipping.shippingAddress.phoneNumber,
                email = shipping.shippingAddress.email,
                address = shipping.shippingAddress.address,
                postalCode = shipping.shippingAddress.postalCode,
                city = shipping.shippingAddress.city,
                country = shipping.shippingAddress.country
            )
        )
        ShippingMethodType.PERSONAL_DELIVERY -> PersonalShipping(
            id = ShippingId(UUID.randomUUID().toString()),
            shippingMethodId = shipping.shippingMethodId,
            shippingMethodName = shipping.shippingMethodName,
            cost = Shipping.Cost(shipping.cost.finalCost)
        )
    }.also { shippingFacade.save(it) }

    private fun reserveOffers(
        order: PlaceOrdersCommand.Order,
        offers: List<Offer>
    ) : List<StockReservation> = order.items.map {
        val correspondingOffer = offers.first { offer -> offer.id == it.offerId }
        stockFacade.reserve(correspondingOffer.stockId, it.quantity)
    }

    fun getOrder(orderId: OrderId, username: String): Order {
        val user = userFacade.getUserByUsername(username)
        val order = orderRepository.findById(orderId) ?: throw OrderNotFoundException(orderId)
        checkIfOrderBelongsToUser(user, order)
        return order
    }

    fun getOrderSnippet(orderId: OrderId, username: String): OrderSnippet {
        val user = userFacade.getUserByUsername(username)
        val order = orderRepository.findById(orderId) ?: throw OrderNotFoundException(orderId)
        val seller = userFacade.getUserById(order.seller.id)
        val shipping = shippingFacade.findShipping(order.shipping.id)
        val items = order.items.map { item -> toOrderSnippetItem(item) }

        return order.toOrderSnippet(seller, user, shipping, items)
    }

    fun getUserOrders(username: String, itemsPerPage: Int?, page: Int?): Page<Order> {
        val userId = userFacade.getUserByUsername(username).id
        return orderRepository.findByBuyerId(userId, itemsPerPage, page, null)
    }

    fun getUserOrdersSnippets(username: String, itemsPerPage: Int?, page: Int?): Page<OrderSnippet> {
        val user = userFacade.getUserByUsername(username)
        return orderRepository.findByBuyerId(user.id, itemsPerPage, page, null).map {
            val seller = userFacade.getUserById(it.seller.id)
            val shipping = shippingFacade.findShipping(it.shipping.id)
            val items = it.items.map { item -> toOrderSnippetItem(item) }
            it.toOrderSnippet(seller, user, shipping, items)
        }
    }

    fun getSellerOrders(username: String, itemsPerPage: Int?, page: Int?): Page<Order> {
        val sellerId = userFacade.getUserByUsername(username).id
        return orderRepository.findBySellerId(sellerId, itemsPerPage, page, null)
    }

    fun getSellerOrdersSnippets(username: String, itemsPerPage: Int?, page: Int?): Page<OrderSnippet> {
        val user = userFacade.getUserByUsername(username)
        return orderRepository.findBySellerId(user.id, itemsPerPage, page, null).map {
            val shipping = shippingFacade.findShipping(it.shipping.id)
            val items = it.items.map { item -> toOrderSnippetItem(item) }
            val buyer = userFacade.getUserById(it.buyer.id)
            it.toOrderSnippet(user, buyer, shipping, items)
        }
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

private fun Order.toOrderSnippet(
    seller: User,
    buyer: User,
    shipping: Shipping,
    orderItems: List<OrderSnippet.OrderItem>
) = OrderSnippet(
    id = this.id,
    buyer = OrderSnippet.Buyer(
        id = this.buyer.id,
        name = buyer.username,
        firstName = buyer.firstName,
        lastName = buyer.lastName
    ),
    seller = OrderSnippet.Seller(seller.id, seller.username, seller.firstName, seller.lastName),
    shipping = OrderSnippet.Shipping(
        id = this.shipping.id,
        methodName = shipping.shippingMethodName,
        methodType = when (shipping) {
            is PersonalShipping -> ShippingMethodType.PERSONAL_DELIVERY
            is AddressShipping -> ShippingMethodType.ADDRESS_DELIVERY
            is PickupPointShipping -> ShippingMethodType.PICKUP_DELIVERY
            else -> throw IllegalStateException("")
        },
        shippingAddress = shipping.let {
            if (it is AddressShipping) {
                OrderSnippet.ShippingAddress(
                    firstAndLastName = it.address.firstAndLastName,
                    phoneNumber = it.address.phoneNumber,
                    email = it.address.email,
                    address = it.address.address,
                    postalCode = it.address.postalCode,
                    city = it.address.city,
                    country = it.address.country
                )
            } else null
        },
        pickupPoint = shipping.let {
           if (it is PickupPointShipping) {
               OrderSnippet.PickupPoint(
                   firstAndLastName = it.pickupPoint.firstAndLastName,
                   phoneNumber = it.pickupPoint.phoneNumber,
                   email = it.pickupPoint.email,
                   pickupPointId = it.pickupPoint.pickupPointId
               )
           } else null
        },
        cost = OrderSnippet.Cost(shipping.cost.finalCost)
    ),
    items = orderItems,
    orderType = this.orderType,
    exchangeBooks = this.exchangeBooks,
    orderDate = this.orderDate,
    status = this.status,
    totalCost = this.totalCost,
    note = this.note
)

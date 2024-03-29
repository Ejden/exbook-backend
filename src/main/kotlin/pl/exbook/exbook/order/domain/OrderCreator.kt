package pl.exbook.exbook.order.domain

import mu.KLogging
import java.time.Instant
import java.util.UUID
import org.springframework.stereotype.Service
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.OrderFacade
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
import pl.exbook.exbook.user.domain.User

@Service
class OrderCreator(
    private val offerFacade: OfferFacade,
    private val orderValidator: OrderValidator,
    private val orderRepository: OrderRepository,
    private val shippingFacade: ShippingFacade,
    private val stockFacade: StockFacade,
    private val orderFactory: OrderFactory
) {
    companion object : KLogging()
    fun placeOrders(command: PlaceOrdersCommand): OrdersCreationResult {
        val orders = mutableListOf<Order>()
        val errors = mutableMapOf<OrderId, Exception>()

        command.orders.forEach { order ->
            try {
                orders += placeOrder(order, command.buyer, command.timestamp)
            } catch (cause: Exception) {
                logger.error(cause) { "Error creating order ${order.orderId.raw} from purchase ${command.purchaseId.raw}" }
                errors[order.orderId] = cause
            }
        }

        return OrdersCreationResult(createdOrders = orders, errors = errors)
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
                    OrderFacade.logger.error(cause) { "Unable to cancel reservation ${it.reservationId.raw} after order creation error" }
                }
            }

            try {
                shipping?.let { shippingFacade.remove(it.id) }
            } catch (cause: Exception) {
                OrderFacade.logger.error(cause) { "Unable to remove shipping ${shipping?.id} after order creation error" }
            }

            try {
                order?.let { orderRepository.remove(it.id) }
            } catch (cause: Exception) {
                OrderFacade.logger.error(cause) { "Unable to remove order ${order?.id} after order creation error" }
            }

            throw cause
        }
    }

    private fun createShippingFromDraft(shipping: PlaceOrdersCommand.Shipping): Shipping =
        when (shipping.shippingMethodType) {
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
    ): List<StockReservation> = order.items.map {
        val correspondingOffer = offers.first { offer -> offer.id == it.offerId }
        stockFacade.reserve(correspondingOffer.stockId, it.quantity)
    }

    private fun confirmOffersReservations(stockReservations: List<StockReservation>) = stockReservations.forEach {
        stockFacade.confirmReservation(it.reservationId)
    }
}

package pl.exbook.exbook.order.domain

import mu.KLogging
import org.springframework.stereotype.Service
import pl.exbook.exbook.order.adapter.mongodb.OrderNotFoundException
import pl.exbook.exbook.order.domain.Order.OrderStatus
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.user.UserFacade

@Service
class OrderStatusService(
    private val orderRepository: OrderRepository,
    private val userFacade: UserFacade,
    private val shippingFacade: ShippingFacade,
    private val statusChangeValidator: OrderStatusChangeValidator
) {
    fun changeStatus(command: OrderStatusChangeCommand): Order = when (command.newStatus) {
        OrderStatus.NEW, OrderStatus.WAITING_FOR_ACCEPT -> throw IllegalStatusChangeException()
        else -> {
            val user = userFacade.getUserByUsername(command.username)
            val order = orderRepository.findById(command.orderId) ?: throw OrderNotFoundException(command.orderId)
            statusChangeValidator.validateStatusChange(command, user, order)

            order.changeStatus(command.newStatus).let { orderRepository.save(it) }
        }
    }

    fun acceptExchange(command: AcceptExchangeCommand): Order {
        val seller = userFacade.getUserByUsername(command.username)
        val order = orderRepository.findById(command.orderId) ?: throw OrderNotFoundException(command.orderId)
        val shipping = shippingFacade.findShipping(order.shipping.id)
        statusChangeValidator.validateStatusChangeToAccepted(command, seller, order, shipping)

        val updatedOrder = order.markAsAccepted(command.toSellerShippingInfo())
        val savedOrder = orderRepository.save(updatedOrder)
        logger.info { "Seller ${seller.id} accepted exchange for order ${order.id}" }
        return savedOrder
    }

    companion object : KLogging() {}
}

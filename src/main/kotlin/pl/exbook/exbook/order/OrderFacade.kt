package pl.exbook.exbook.order

import mu.KLogging
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import pl.exbook.exbook.order.adapter.mongodb.OrderNotFoundException
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.order.domain.OrderCreator
import pl.exbook.exbook.order.domain.OrderDecorator
import pl.exbook.exbook.order.domain.OrderRepository
import pl.exbook.exbook.order.domain.OrderSnippet
import pl.exbook.exbook.order.domain.OrderStatusChangeCommand
import pl.exbook.exbook.order.domain.OrderStatusService
import pl.exbook.exbook.order.domain.PlaceOrdersCommand
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.user.UserFacade

@Service
class OrderFacade(
    private val orderRepository: OrderRepository,
    private val userFacade: UserFacade,
    private val shippingFacade: ShippingFacade,
    private val orderStatusService: OrderStatusService,
    private val orderCreator: OrderCreator,
    private val orderDecorator: OrderDecorator
) {
    fun placeOrders(command: PlaceOrdersCommand): List<Order> = orderCreator.placeOrders(command)

    fun getOrderSnippet(orderId: OrderId, username: String): OrderSnippet {
        val buyer = userFacade.getUserByUsername(username)
        val order = orderRepository.findById(orderId) ?: throw OrderNotFoundException(orderId)
        val seller = userFacade.getUserById(order.seller.id)
        val shipping = shippingFacade.findShipping(order.shipping.id)

        return orderDecorator.decorate(order, seller, buyer, shipping)
    }

    fun getUserOrders(username: String, itemsPerPage: Int?, page: Int?): Page<Order> {
        val userId = userFacade.getUserByUsername(username).id
        return orderRepository.findByBuyerId(userId, itemsPerPage, page, null)
    }

    fun getUserOrdersSnippets(
        username: String,
        itemsPerPage: Int?,
        page: Int?,
        statusFilters: List<Order.OrderStatus>
    ): Page<OrderSnippet> {
        val user = userFacade.getUserByUsername(username)
        val orders = if (statusFilters.isEmpty()) orderRepository.findByBuyerId(user.id, itemsPerPage, page, null)
        else orderRepository.findByBuyerIdAndStatus(user.id, statusFilters, itemsPerPage, page, null)

        return orders.map {
            val seller = userFacade.getUserById(it.seller.id)
            val shipping = shippingFacade.findShipping(it.shipping.id)
            orderDecorator.decorate(it, seller, user, shipping)
        }
    }

    fun getSellerOrders(username: String, itemsPerPage: Int?, page: Int?): Page<Order> {
        val sellerId = userFacade.getUserByUsername(username).id
        return orderRepository.findBySellerId(sellerId, itemsPerPage, page, null)
    }

    fun getSellerOrdersSnippets(
        username: String,
        itemsPerPage: Int?,
        page: Int?,
        statusFilters: List<Order.OrderStatus>
    ): Page<OrderSnippet> {
        val user = userFacade.getUserByUsername(username)
        val orders = if (statusFilters.isEmpty()) orderRepository.findBySellerId(user.id, itemsPerPage, page, null)
        else orderRepository.findBySellerIdAndStatus(user.id, statusFilters, itemsPerPage, page, null)

        return orders.map {
            val shipping = shippingFacade.findShipping(it.shipping.id)
            val buyer = userFacade.getUserById(it.buyer.id)
            orderDecorator.decorate(it, user, buyer, shipping)
        }
    }

    fun changeOrderStatus(command: OrderStatusChangeCommand) = orderStatusService.changeStatus(command).decorate()

    private fun Order.decorate() = orderDecorator.decorate(
        order = this,
        seller = userFacade.getUserById(this.seller.id),
        buyer = userFacade.getUserById(this.buyer.id),
        shipping = shippingFacade.findShipping(this.shipping.id)
    )

    companion object : KLogging()
}

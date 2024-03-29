package pl.exbook.exbook.order.domain

import org.springframework.data.domain.Page
import pl.exbook.exbook.order.domain.Order.OrderStatus
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.UserId

interface OrderRepository {

    fun findById(id: OrderId): Order?

    fun save(order: Order): Order

    fun findByBuyerId(buyerId: UserId, itemsPerPage: Int?, page: Int?, sorting: String?): Page<Order>

    fun findByBuyerIdAndStatus(
        buyerId: UserId,
        status: List<OrderStatus>,
        itemsPerPage: Int?,
        page: Int?,
        sorting: String?
    ): Page<Order>

    fun findBySellerId(sellerId: UserId, itemsPerPage: Int?, page: Int?, sorting: String?): Page<Order>

    fun findBySellerIdAndStatus(
        sellerId: UserId,
        status: List<OrderStatus>,
        itemsPerPage: Int?,
        page: Int?,
        sorting: String?
    ): Page<Order>

    fun remove(orderId: OrderId)
}

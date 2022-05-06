package pl.exbook.exbook.order.domain

import pl.exbook.exbook.shared.OrderId

data class OrderStatusChangeCommand(
    val orderId: OrderId,
    val username: String,
    val newStatus: Order.OrderStatus
)

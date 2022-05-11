package pl.exbook.exbook.order.adapter.rest.dto

import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.order.domain.OrderStatusChangeCommand
import pl.exbook.exbook.shared.OrderId

data class OrderStatusChangeRequest(
    val newStatus: String
) {
    fun toCommand(orderId: OrderId, username: String) = OrderStatusChangeCommand(
        orderId = orderId,
        username = username,
        newStatus = Order.OrderStatus.valueOf(this.newStatus)
    )
}

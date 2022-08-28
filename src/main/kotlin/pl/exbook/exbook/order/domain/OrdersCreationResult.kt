package pl.exbook.exbook.order.domain

import pl.exbook.exbook.shared.OrderId

data class OrdersCreationResult(
    val createdOrders: List<Order>,
    val errors: Map<OrderId, Exception>
)

package pl.exbook.exbook.order.domain

import pl.exbook.exbook.shared.OrderId

interface OrderRepository {

    fun findById(id: OrderId): Order?

    fun save(order: Order): Order
}

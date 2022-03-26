package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.UserId
import java.time.Instant

data class PreviewAvailableShippingCommand(
    val timestamp: Instant = Instant.now(),
    val orders: Map<OrderKey, Order>
) {
    data class OrderKey(
        val sellerId: UserId,
        val orderType: OrderType
    )

    data class Order(
        val offers: List<Offer>
    )
}

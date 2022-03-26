package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.UserId

data class AvailableShipping(
    val shippingByOrders: Map<OrderKey, List<ShippingOption>>
) {
    data class OrderKey(
        val sellerId: UserId,
        val orderType: OrderType
    )

    data class ShippingOption(
        val methodId: ShippingMethodId,
        val methodName: String,
        val pickupPoint: Boolean,
        val price: Money
    )
}

package pl.exbook.exbook.baskettransaction.domain

import java.time.Instant
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.ShippingMethodId

data class PreviewBasketTransactionCommand(
    val buyer: Buyer,
    val orders: List<Order>
) {
    val timestamp = Instant.now()

    data class Buyer(
        val username: String
    )

    data class Order(
        val offerId: OfferId,
        val quantity: Int,
        val orderType: OrderType,
        val shipping: Shipping
    )

    data class Shipping(
        val shippingMethodId: ShippingMethodId
    )
}

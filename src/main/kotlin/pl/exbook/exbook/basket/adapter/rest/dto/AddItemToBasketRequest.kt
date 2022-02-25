package pl.exbook.exbook.basket.adapter.rest.dto

import pl.exbook.exbook.basket.domain.AddItemToBasketCommand
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.OfferId

data class AddItemToBasketRequest(
    val offerId: String,
    val orderType: String,
    val quantity: Long
) {
    fun toCommand(username: String) = AddItemToBasketCommand(
        username = username,
        offerId = OfferId(this.offerId),
        quantity = this.quantity,
        orderType = OrderType.valueOf(this.orderType)
    )
}

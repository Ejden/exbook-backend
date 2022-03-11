package pl.exbook.exbook.basket.adapter.rest.dto

import pl.exbook.exbook.basket.domain.ChangeItemQuantityCommand
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.OfferId

data class ChangeItemQuantityRequest(
    val orderType: String,
    val newQuantity: Long,
) {
    fun toCommand(offerId: OfferId, username: String) = ChangeItemQuantityCommand(
        username = username,
        offerId = offerId,
        orderType = OrderType.valueOf(this.orderType),
        newQuantity = newQuantity
    )
}

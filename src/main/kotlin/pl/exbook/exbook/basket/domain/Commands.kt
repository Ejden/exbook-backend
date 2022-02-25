package pl.exbook.exbook.basket.domain

import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.OfferId

data class AddItemToBasketCommand(
    val username: String,
    val offerId: OfferId,
    val quantity: Long,
    val orderType: Order.OrderType
)

data class ChangeItemQuantityCommand(
    val username: String,
    val offerId: OfferId,
    val orderType: Order.OrderType,
    val newQuantity: Long
)

package pl.exbook.exbook.basket.domain

import pl.exbook.exbook.shared.OfferId

data class AddItemToBasketCommand(
    val offerId: OfferId,
    val quantity: Long
)

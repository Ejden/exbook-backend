package pl.exbook.exbook.basket.domain

import java.lang.RuntimeException
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order

class BasketValidator {

    fun validateAddingItem(offer: Offer, command: AddItemToBasketCommand) {
        checkOfferType(offer, command.orderType)
    }

    private fun checkOfferType(offer: Offer, orderType: Order.OrderType) {
        when (orderType) {
            Order.OrderType.BUY -> if (!offer.canBeBought()) throw BasketValidationException(
                "Tried to add offer ${offer.id.raw} to basket with buy type when offer cannot be bought"
            )
            Order.OrderType.EXCHANGE -> if(!offer.canBeExchanged()) throw BasketValidationException(
                "Tried to add offer ${offer.id.raw} to basket with exchange type when offer cannot be exchanged"
            )
        }
    }
}

class BasketValidationException(errorMsg: String) : RuntimeException(errorMsg)

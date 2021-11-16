package pl.exbook.exbook.basket.domain

import java.lang.RuntimeException
import pl.exbook.exbook.offer.domain.Offer

class BasketValidator {

    fun validate(offer: Offer) {
        checkThatOfferCanBeBought(offer)
    }

    private fun checkThatOfferCanBeBought(offer: Offer) {
        if (!offer.canBeBought()) throw BasketValidationException("Tried to add offer ${offer.id} to basket when offer cannot be bought")
    }
}

class BasketValidationException(errorMsg: String) : RuntimeException(errorMsg)

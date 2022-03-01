package pl.exbook.exbook.basket.domain

import org.springframework.stereotype.Service
import java.lang.RuntimeException
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.domain.User

@Service
class BasketValidator {
    fun validateAddingItem(offer: Offer, buyer: User, command: AddItemToBasketCommand) {
        checkOfferType(offer, command.orderType)
        checkThatOfferDoesntBelongToBuyer(offer, buyer.id)
        checkPositiveQuantity(buyer, command)
    }

    fun validateItemQuantityChange(buyerId: UserId, command: ChangeItemQuantityCommand) {
        checkPositiveQuantity(buyerId, command)
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

    private fun checkThatOfferDoesntBelongToBuyer(offer: Offer, buyerId: UserId) {
        if (buyerId == offer.seller.id) {
            throw BasketValidationException("Tried to add to basket ${offer.id.raw} that belongs to buyer ${buyerId.raw}")
        }
    }

    private fun checkPositiveQuantity(buyer: User, command: AddItemToBasketCommand) {
        if (command.quantity <= 0) {
            throw BasketValidationException(
                "Buyer ${buyer.id.raw} Tried to add to basket offer ${command.offerId.raw} with non positive quantity"
            )
        }
    }

    private fun checkPositiveQuantity(buyerId: UserId, command: ChangeItemQuantityCommand) {
        if (command.newQuantity < 0) {
            throw BasketValidationException(
                "Buyer ${buyerId.raw} Tried to change item quantity for offer ${command.offerId.raw} with non positive quantity"
            )
        }
    }
}

class BasketValidationException(errorMsg: String) : RuntimeException(errorMsg)

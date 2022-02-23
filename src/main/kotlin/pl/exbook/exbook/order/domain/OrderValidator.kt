package pl.exbook.exbook.order.domain

import pl.exbook.exbook.offer.domain.Offer
import java.lang.RuntimeException

class OrderValidator {
    fun validate(command: PlaceOrdersCommand.Order, offers: List<Offer>) {
        validateNumberOfItems(command.items)
        validateItemsQuantity(command.items)
        validateOrderType(command, offers)
        validateSeller(offers, command.seller)
    }

    private fun validateNumberOfItems(items: List<PlaceOrdersCommand.Item>) {
        if (items.isEmpty()) {
            throw OrderValidationFailedException("There are no items in order")
        }
    }

    private fun validateItemsQuantity(items: List<PlaceOrdersCommand.Item>) {
        if (items.any { it.quantity <= 0 }) {
            throw OrderValidationFailedException("Order item quantity is below or equal to 0")
        }
    }

    private fun validateOrderType(order: PlaceOrdersCommand.Order, offers: List<Offer>) {
        when (order.orderType) {
            Order.OrderType.BUY -> {
                if (offers.any { !it.canBeBought() }) {
                    throw OrderValidationFailedException("At least one offer from $offers cannot be bought")
                }
                if (order.exchangeBooks.isNotEmpty()) {
                    throw OrderValidationFailedException("Cannot buy offers with exchanging book provided")
                }
            }
            Order.OrderType.EXCHANGE -> {
                if (offers.any { !it.canBeExchanged() }) {
                    throw OrderValidationFailedException("At least one offer from $offers cannot be exchanged")
                }
                if (order.exchangeBooks.isEmpty()) {
                    throw OrderValidationFailedException("Cannot exchange books without any exchange book provided")
                }
            }
        }
    }

    private fun validateSeller(offers: List<Offer>, seller: PlaceOrdersCommand.Seller) {
        if (offers.any { it.seller.id != seller.id }) {
            throw OrderValidationFailedException("Offers $offers are not from the same seller ${seller.id}")
        }
    }
}

class OrderValidationFailedException(errorMsg: String, cause: Throwable? = null) : RuntimeException(errorMsg, cause)

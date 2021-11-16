package pl.exbook.exbook.order.domain

import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.adapter.rest.NewOrderDto
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId
import java.lang.RuntimeException

class OrderValidator {

    fun validate(newOrder: NewOrderDto, offers: List<Offer>) {
        checkItemsQuantity(newOrder.items)
        checkOrderType(newOrder.items, offers)
        checkThatOffersAreFromTheSameSeller(offers, newOrder.seller)
    }

    private fun checkItemsQuantity(orderItems: List<NewOrderDto.OrderItemDto>) {
        if (orderItems.any { it.quantity <= 0 }) throw OrderValidationFailedException("Order item quantity is below or equal to 0")
    }

    private fun checkOrderType(orderItems: List<NewOrderDto.OrderItemDto>, offers: List<Offer>) {
        val orderItemsByOfferId = orderItems.associateBy { OfferId(it.offerId) }
        val offersByOfferId = offers.associateBy { it.id }

        val items = (orderItemsByOfferId.keys + offersByOfferId.keys)
            .toSet()
            .associateWith { Pair(orderItemsByOfferId[it]!!, offersByOfferId[it]!!) }
            .values

        items.forEach {
            if (it.first.orderType == Order.OrderType.BUY.name && !it.second.canBeBought()) {
                throw OrderValidationFailedException("Can't buy offer ${it.second.id} with order type = ${it.second.type}")
            }

            if (it.first.orderType == Order.OrderType.EXCHANGE.name && !it.second.canBeExchanged()) {
                throw OrderValidationFailedException("Can't exchange offer ${it.second.id} with order type = ${it.second.type}")
            }

            if (it.first.orderType == Order.OrderType.BUY.name && it.first.exchangeBook != null) {
                throw OrderValidationFailedException("Can't buy offer ${it.second.id} with exchanging book")
            }

            if (it.first.orderType == Order.OrderType.EXCHANGE.name && it.first.exchangeBook == null) {
                throw OrderValidationFailedException("Exchange book not provided for exchange offer ${it.second.id}")
            }
        }
    }

    private fun checkThatOffersAreFromTheSameSeller(offers: List<Offer>, seller: NewOrderDto.SellerDto) {
        if (!offers.all { it.seller.id == UserId(seller.id) }) {
            throw OrderValidationFailedException("Offers $offers are not from the same seller ${seller.id}")
        }
    }
}

class OrderValidationFailedException(errorMsg: String, cause: Throwable? = null) : RuntimeException(errorMsg, cause)

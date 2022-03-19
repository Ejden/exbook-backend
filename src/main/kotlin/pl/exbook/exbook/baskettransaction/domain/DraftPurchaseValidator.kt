package pl.exbook.exbook.baskettransaction.domain

import org.springframework.stereotype.Component
import pl.exbook.exbook.basket.domain.Basket
import pl.exbook.exbook.order.domain.Order

@Component
class DraftPurchaseValidator {
    fun validatePreview(command: PreviewBasketTransactionCommand) {
        validateNumberOfItemGroups(command.basket.itemsGroups)
        validateOrderType(command)
        validateSeller(command)
        validateExchangeBooks(command)
        validateItemsQuantity(command)
        validateShipping(command)
    }

    private fun validateNumberOfItemGroups(itemsGroups: MutableMap<Basket.ItemsGroupKey, Basket.ItemsGroup>) {
        if (itemsGroups.isEmpty()) {
            throw DraftPurchaseValidationException("Cannot create draft purchase with 0 items groups")
        }
    }

    private fun validateOrderType(command: PreviewBasketTransactionCommand) {
        command.basket.itemsGroups.forEach { itemsGroup ->
            itemsGroup.value.items.forEach { item ->
                val correspondingOffer = command.offers.firstOrNull { offer -> offer.id == item.offer.id }
                    ?: throw DraftPurchaseValidationException("Cannot find offer with id ${item.offer.id.raw}")
                when(itemsGroup.value.orderType) {
                    Order.OrderType.EXCHANGE -> {
                        if (!correspondingOffer.canBeExchanged()) {
                            throw DraftPurchaseValidationException("Offer ${correspondingOffer.id.raw} cannot be exchanged")
                        }
                    }
                    Order.OrderType.BUY -> {
                        if (!correspondingOffer.canBeBought()) {
                            throw DraftPurchaseValidationException("Offer ${correspondingOffer.id.raw} cannot be bought")
                        }
                    }
                }
            }
        }
    }

    private fun validateSeller(command: PreviewBasketTransactionCommand) {
        command.basket.itemsGroups.forEach { itemsGroup ->
            val sellerId = itemsGroup.key.sellerId
            itemsGroup.value.items.forEach { item ->
                val correspondingOffer = command.offers.firstOrNull { offer -> offer.id == item.offer.id }
                    ?: throw DraftPurchaseValidationException("Cannot find offer with id ${item.offer.id.raw}")
                if (correspondingOffer.seller.id != sellerId) {
                    throw DraftPurchaseValidationException("Offer ${correspondingOffer.id.raw} doesn't belong to seller ${sellerId.raw}")
                }
            }
        }
    }

    private fun validateExchangeBooks(command: PreviewBasketTransactionCommand) {
        val hasEmptyExchangeBooks = command.basket.itemsGroups.filter { it.value.orderType == Order.OrderType.EXCHANGE }
            .any { it.value.exchangeBooks.isEmpty() }
        if (hasEmptyExchangeBooks) {
            throw DraftPurchaseValidationException("Cannot create draft order with empty exchange books in items group")
        }
    }

    private fun validateItemsQuantity(command: PreviewBasketTransactionCommand) {
        val hasEmptyGroup = command.basket.itemsGroups.any { it.value.items.isEmpty() }
        if (hasEmptyGroup) {
            throw DraftPurchaseValidationException("Cannot create draft order with empty group")
        }
    }

    private fun validateShipping(command: PreviewBasketTransactionCommand) {
        if (command.shipping.size != command.basket.itemsGroups.size) {
            throw DraftPurchaseValidationException("shipping items size is different than itemsGroups size")
        }
        command.basket.itemsGroups.forEach { itemsGroup ->
            command.shipping.firstOrNull { shipping -> shipping.sellerId == itemsGroup.key.sellerId && shipping.orderType == itemsGroup.key.orderType }
                ?: throw DraftPurchaseValidationException(
                    "Can't find shipping for seller ${itemsGroup.key.sellerId.raw} with order type ${itemsGroup.key.orderType}"
                )
        }
    }
}

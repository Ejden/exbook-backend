package pl.exbook.exbook.baskettransaction.domain

import org.springframework.stereotype.Service
import pl.exbook.exbook.basket.BasketFacade

@Service
class BasketTransactionFacade(
    private val basketFacade: BasketFacade,
    private val draftOrderCreator: DraftOrderCreator,
) {
    fun previewTransaction(command: PreviewBasketTransactionCommand) {
        val basket = basketFacade.getUserBasket(command.buyer.username)
    }

    fun realiseTransaction() {

    }
}

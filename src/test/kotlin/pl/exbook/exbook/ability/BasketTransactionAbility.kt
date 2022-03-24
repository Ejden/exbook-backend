package pl.exbook.exbook.ability

import io.mockk.mockk
import pl.exbook.exbook.adapters.InMemoryBasketTransactionRepository
import pl.exbook.exbook.basket.BasketFacade
import pl.exbook.exbook.baskettransaction.BasketTransactionFacade
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseCreator
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseDecorator
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseValidator
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.user.UserFacade

class BasketTransactionAbility {
    private val basketFacade = mockk<BasketFacade>()
    private val repository = InMemoryBasketTransactionRepository()
    private val shippingFacade = mockk<ShippingFacade>()
    private val draftPurchaseValidator = DraftPurchaseValidator()
    private val draftPurchaseCreator = DraftPurchaseCreator(repository, shippingFacade, draftPurchaseValidator)
    private val userFacade = mockk<UserFacade>()
    private val offerFacade = mockk<OfferFacade>()
    private val draftPurchaseDecorator = DraftPurchaseDecorator()
    val basketTransactionFacade = BasketTransactionFacade(
        basketFacade = basketFacade,
        draftPurchaseCreator = draftPurchaseCreator,
        userFacade = userFacade,
        offerFacade = offerFacade,
        draftPurchaseDecorator = draftPurchaseDecorator
    )
}

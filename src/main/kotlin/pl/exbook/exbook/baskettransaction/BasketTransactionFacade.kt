package pl.exbook.exbook.baskettransaction

import java.time.Instant
import org.springframework.stereotype.Service
import pl.exbook.exbook.basket.BasketFacade
import pl.exbook.exbook.baskettransaction.domain.DetailedDraftPurchase
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseCreator
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseDecorator
import pl.exbook.exbook.baskettransaction.domain.DraftPurchase
import pl.exbook.exbook.baskettransaction.domain.PreviewBasketTransactionCommand
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.user.UserFacade

@Service
class BasketTransactionFacade(
    private val basketFacade: BasketFacade,
    private val draftPurchaseCreator: DraftPurchaseCreator,
    private val userFacade: UserFacade,
    private val offerFacade: OfferFacade,
    private val draftPurchaseDecorator: DraftPurchaseDecorator
) {
    fun previewTransaction(username: String, shipping: List<PreviewBasketTransactionCommand.Shipping>): DetailedDraftPurchase {
        val timestamp = Instant.now()
        val buyer = userFacade.getUserByUsername(username)
        val basket = basketFacade.getUserBasket(username)
        val offers = basket.itemsGroups.values
            .asSequence()
            .flatMap { it.items }
            .distinctBy { it.offer.id }
            .map { offerFacade.getOfferVersion(it.offer.id, timestamp) }
            .toList()

        val draftPurchase = draftPurchaseCreator.createDraftPurchase(
            PreviewBasketTransactionCommand(buyer, basket, offers, shipping, timestamp)
        )

        return draftPurchaseDecorator.decorateWithDetails(draftPurchase, offers)
    }
}

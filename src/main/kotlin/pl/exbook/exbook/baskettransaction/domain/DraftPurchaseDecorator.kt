package pl.exbook.exbook.baskettransaction.domain

import org.springframework.stereotype.Component
import pl.exbook.exbook.offer.domain.Offer

@Component
class DraftPurchaseDecorator {
    fun decorateWithDetails(draftPurchase: DraftPurchase, offers: List<Offer>): DetailedDraftPurchase {
        throw NotImplementedError()
    }
}

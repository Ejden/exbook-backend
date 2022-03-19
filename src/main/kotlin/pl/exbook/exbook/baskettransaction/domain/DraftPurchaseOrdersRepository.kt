package pl.exbook.exbook.baskettransaction.domain

import pl.exbook.exbook.shared.PurchaseId
import pl.exbook.exbook.shared.UserId

interface DraftPurchaseOrdersRepository {
    fun getDraftPurchase(purchaseId: PurchaseId): DraftPurchase?

    fun getDraftPurchaseForUser(userId: UserId): DraftPurchase?

    fun saveDraftPurchase(purchase: DraftPurchase): DraftPurchase
}

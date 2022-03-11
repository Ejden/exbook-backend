package pl.exbook.exbook.baskettransaction.domain

import pl.exbook.exbook.shared.PurchaseId

interface DraftPurchaseOrdersRepository {
    fun getDraftPurchase(purchaseId: PurchaseId): DraftPurchase?

    fun saveDraftPurchase(purchase: DraftPurchase): DraftPurchase
}

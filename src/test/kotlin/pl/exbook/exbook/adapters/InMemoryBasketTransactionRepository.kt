package pl.exbook.exbook.adapters

import pl.exbook.exbook.baskettransaction.domain.DraftPurchase
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseOrdersRepository
import pl.exbook.exbook.shared.PurchaseId
import pl.exbook.exbook.shared.UserId

class InMemoryBasketTransactionRepository : DraftPurchaseOrdersRepository {
    private val memory = mutableMapOf<PurchaseId, DraftPurchase>()

    override fun getDraftPurchase(purchaseId: PurchaseId): DraftPurchase? = memory[purchaseId]

    override fun getDraftPurchaseForUser(userId: UserId): DraftPurchase? =
        memory.values.firstOrNull { it.buyer.id == userId }

    override fun saveDraftPurchase(purchase: DraftPurchase): DraftPurchase {
        memory[purchase.purchaseId] = purchase
        return memory[purchase.purchaseId]!!
    }
}

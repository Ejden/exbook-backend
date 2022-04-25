package pl.exbook.exbook.baskettransaction.domain

import pl.exbook.exbook.shared.OrderId

interface PurchaseCreationResult {
    val result: CreationResult
}

class UnsuccessfulPurchaseCreationResult(
    val reason: PurchaseNotCreatedReason
) : PurchaseCreationResult {
    override val result: CreationResult = CreationResult.NOT_CREATED
}

class SuccessfulPurchaseCreationResult(
    val createdOrders: List<OrderId>
) : PurchaseCreationResult {
    override val result: CreationResult = CreationResult.CREATED
}

enum class CreationResult {
    CREATED,
    NOT_CREATED
}

enum class PurchaseNotCreatedReason {
    DRAFT_TOO_OLD,
    EMPTY_DRAFT,
    DELIVERY_INFO_NOT_COMPLETE
}

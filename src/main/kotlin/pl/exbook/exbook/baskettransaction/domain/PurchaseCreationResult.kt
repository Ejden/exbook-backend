package pl.exbook.exbook.baskettransaction.domain

import pl.exbook.exbook.shared.OrderId

sealed class PurchaseCreationResult(
    val result: CreationResult,
    val numberOfCreatedOrders: Int,
    val numberOfFailedOrders: Int,
    val createdOrders: List<OrderId>,
    val errorsByOrder: Map<OrderId, OrderNotCreatedReason>,
    val purchaseCreationError: PurchaseNotCreatedReason?
) {
    class AllOrdersCreated(
        createdOrders: List<OrderId>
    ) : PurchaseCreationResult(
        result = CreationResult.ALL_ORDERS_CREATED,
        numberOfCreatedOrders = createdOrders.size,
        numberOfFailedOrders = 0,
        createdOrders = createdOrders,
        errorsByOrder = emptyMap(),
        purchaseCreationError = null
    )

    class SomeOrdersCreated(
        createdOrders: List<OrderId>,
        errors: Map<OrderId, OrderNotCreatedReason>
    ) : PurchaseCreationResult(
        result = CreationResult.SOME_ORDERS_CREATED,
        numberOfCreatedOrders = createdOrders.size,
        numberOfFailedOrders = errors.size,
        createdOrders = createdOrders,
        errorsByOrder = errors,
        purchaseCreationError = null
    )

    class NoneOrdersCreated(
        errors: Map<OrderId, OrderNotCreatedReason>
    ) : PurchaseCreationResult(
        result = CreationResult.NONE_ORDER_CREATED,
        numberOfCreatedOrders = 0,
        numberOfFailedOrders = errors.size,
        createdOrders = emptyList(),
        errorsByOrder = errors,
        purchaseCreationError = null
    )

    class PurchaseCreationError(
        purchaseCreationError: PurchaseNotCreatedReason,
        numbersOfOrders: Int
    ) : PurchaseCreationResult(
        result = CreationResult.NONE_ORDER_CREATED,
        numberOfCreatedOrders = 0,
        numberOfFailedOrders = numbersOfOrders,
        createdOrders = emptyList(),
        errorsByOrder = emptyMap(),
        purchaseCreationError = purchaseCreationError
    )
}

enum class CreationResult {
    ALL_ORDERS_CREATED,
    SOME_ORDERS_CREATED,
    NONE_ORDER_CREATED
}

enum class OrderNotCreatedReason(val userMessageKey: String) {
    NOT_SUFFICIENT_OFFER_QUANTITY("validation.purchaseValidation.insufficientStock.userMessage"),
    UNKNOWN_ERROR("validation.purchaseValidation.unknownOrderError.userMessage")
}

enum class PurchaseNotCreatedReason(val userMessageKey: String) {
    EMPTY_DRAFT("validation.purchaseValidation.emptyDraft.userMessage"),
    DRAFT_TOO_OLD("validation.purchaseValidation.draftToOld.userMessage"),
    DELIVERY_INFO_NOT_COMPLETE("validation.purchaseValidation.deliveryInfoNotComplete.userMessage"),
    UNKNOWN_ERROR("validation.purchaseValidation.unknownPurchaseError.userMessage")
}

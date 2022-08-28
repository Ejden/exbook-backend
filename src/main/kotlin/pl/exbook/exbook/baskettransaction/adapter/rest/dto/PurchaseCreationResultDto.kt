package pl.exbook.exbook.baskettransaction.adapter.rest.dto

data class PurchaseCreationResultDto(
    val result: String,
    val numberOfCreatedOrders: Int,
    val numberOfFailedOrders: Int,
    val createdOrders: List<String>,
    val errorsByOrder: Map<String, OrderCreationError>,
    val purchaseCreationError: PurchaseCreationError?
)

data class OrderCreationError(
    val code: String,
    val userMessage: String
)

data class PurchaseCreationError(
    val code: String,
    val userMessage: String
)

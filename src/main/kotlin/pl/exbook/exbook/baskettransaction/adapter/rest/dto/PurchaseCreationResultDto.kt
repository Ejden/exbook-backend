package pl.exbook.exbook.baskettransaction.adapter.rest.dto

interface PurchaseCreationResultDto {
    val result: String
}

class UnsuccessfulPurchaseCreationResultDto(
    override val result: String,
    val reason: String,
) : PurchaseCreationResultDto

class SuccessfulPurchaseCreationResultDto(
    override val result: String,
    val createdOrders: List<Order>,
) : PurchaseCreationResultDto {
    class Order(
        val id: String
    )
}

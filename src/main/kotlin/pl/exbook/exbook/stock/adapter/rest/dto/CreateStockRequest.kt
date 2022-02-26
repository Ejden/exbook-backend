package pl.exbook.exbook.stock.adapter.rest.dto

data class CreateStockRequest(
    val offerId: String,
    val startQuantity: Int
)

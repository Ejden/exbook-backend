package pl.exbook.exbook.basket.adapter.rest.dto

data class ChangeItemQuantityRequest(
    val orderType: String,
    val newQuantity: Long,
)

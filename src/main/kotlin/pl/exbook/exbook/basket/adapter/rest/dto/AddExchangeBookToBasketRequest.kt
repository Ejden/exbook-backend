package pl.exbook.exbook.basket.adapter.rest.dto

data class AddExchangeBookToBasketRequest(
    val author: String,
    val title: String,
    val isbn: String?,
    val condition: String,
    val quantity: Int
)

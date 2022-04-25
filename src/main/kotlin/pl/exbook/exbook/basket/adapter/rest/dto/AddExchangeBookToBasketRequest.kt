package pl.exbook.exbook.basket.adapter.rest.dto

import pl.exbook.exbook.basket.domain.AddExchangeBookToBasketCommand
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.UserId

data class AddExchangeBookToBasketRequest(
    val author: String,
    val title: String,
    val isbn: String?,
    val condition: String,
    val quantity: Int
) {
    fun toCommand(username: String, sellerId: UserId) = AddExchangeBookToBasketCommand(
        username = username,
        sellerId = sellerId,
        book = AddExchangeBookToBasketCommand.ExchangeBook(
            author = this.author,
            title = this.title,
            isbn = this.isbn,
            condition = Offer.Condition.valueOf(this.condition),
            quantity = this.quantity
        )
    )
}

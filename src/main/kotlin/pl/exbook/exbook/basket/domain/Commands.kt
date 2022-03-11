package pl.exbook.exbook.basket.domain

import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.IllegalParameterException
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId

data class AddItemToBasketCommand(
    val username: String,
    val offerId: OfferId,
    val quantity: Long,
    val orderType: Order.OrderType
)

data class AddExchangeBookToBasketCommand(
    val username: String,
    val sellerId: UserId,
    val book: ExchangeBook
) {
    init {
        if (username.isBlank()) {
            throw IllegalParameterException("username cannot be blank")
        }

        if (book.author.isBlank() || book.author.length >= 256) {
            throw IllegalParameterException("Book author length should be between 1 and 256")
        }

        if (book.title.isBlank() || book.title.length >= 256) {
            throw IllegalParameterException("Book title length should be between 1 and 256")
        }

        if (!((book.isbn == null) || (book.isbn.length == 13) || (book.isbn.length == 10))) {
            throw IllegalParameterException("Book isbn length should be 13 or 10")
        }

        if (book.quantity <= 0) {
            throw IllegalParameterException("Exchange book quantity should be at least 1")
        }
    }

    data class ExchangeBook(
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: Offer.Condition,
        val quantity: Int
    )
}

data class ChangeItemQuantityCommand(
    val username: String,
    val offerId: OfferId,
    val orderType: Order.OrderType,
    val newQuantity: Long
)

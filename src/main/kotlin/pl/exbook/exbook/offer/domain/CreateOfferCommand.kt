package pl.exbook.exbook.offer.domain

import pl.exbook.exbook.offer.adapter.rest.dto.CreateOfferRequest
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.IllegalParameterException
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.dto.toDomain

data class CreateOfferCommand(
    val book: Book,
    val description: String,
    val category: Category,
    val type: Offer.Type,
    val price: Money?,
    val location: String,
    val shippingMethods: List<ShippingMethod>,
    val initialStock: Int
) {
    data class Book(
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: Offer.Condition
    )

    data class ShippingMethod(
        val id: ShippingMethodId,
        val price: Money
    )

    data class Category(val id: CategoryId)

    init {
        if (initialStock <= 0) {
            throw IllegalParameterException("Initial stock should be grater than 0")
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

        if (description.isBlank() || description.length > 2000) {
            throw IllegalParameterException("Description length should be between 1 and 2000")
        }

        when (type) {
            Offer.Type.BUY_ONLY -> {
                if (price == null) {
                    throw IllegalParameterException("Price should not be null with buy only offer type")
                }
            }
            Offer.Type.EXCHANGE_ONLY -> {
                if (price != null) {
                    throw IllegalParameterException("Price should be null with exchange only offer type")
                }
            }
            Offer.Type.EXCHANGE_AND_BUY -> {
                if (price == null) {
                    throw IllegalParameterException("Price should not be null with buy and exchange offer type")
                }
            }
        }

        if (location.isBlank() || location.length > 256) {
            throw IllegalParameterException("Location length should be between 1 and 256")
        }

        if (shippingMethods.isEmpty()) {
            throw IllegalParameterException("Offer should have at least one shipping method")
        }

        if (shippingMethods.any { it.price < Money.zero(it.price.currency) }) {
            throw IllegalParameterException("Shipping method price cannot be less than 0.00")
        }
    }

    companion object {
        fun fromRequest(request: CreateOfferRequest): CreateOfferCommand {
            return CreateOfferCommand(
                book = Book(
                    author = request.book.author,
                    title = request.book.title,
                    isbn = request.book.isbn,
                    condition = Offer.Condition.valueOf(request.book.condition)
                ),
                description = request.description,
                category = Category(CategoryId(request.category.id)),
                type = Offer.Type.valueOf(request.type),
                price = request.price?.toDomain(),
                location = request.location,
                shippingMethods = request.shippingMethods.map { ShippingMethod(ShippingMethodId(it.id), it.price.toDomain()) },
                initialStock = request.initialStock
            )
        }
    }
}

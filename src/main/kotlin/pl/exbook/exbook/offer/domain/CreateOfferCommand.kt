package pl.exbook.exbook.offer.domain

import pl.exbook.exbook.offer.adapter.rest.CreateOfferRequest
import pl.exbook.exbook.shared.CategoryId
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
    val shippingMethods: Collection<ShippingMethod>,
    val initialStock: Int
) {
    data class Book(
        val author: String,
        val title: String,
        val isbn: Long?,
        val condition: Offer.Condition
    )

    data class ShippingMethod(
        val id: ShippingMethodId,
        val price: Money
    )

    data class Category(val id: CategoryId)

    init {
        if (initialStock <= 0) {
            throw InvalidInitialStockException(initialStock)
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

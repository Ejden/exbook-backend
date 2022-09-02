package pl.exbook.exbook.offer.adapter.rest.dto

import pl.exbook.exbook.shared.dto.MoneyDto

data class CreateOfferRequest(
    val book: Book,
    val description: String,
    val category: Category,
    val type: String,
    val images: Images,
    val price: MoneyDto?,
    val location: String,
    val shippingMethods: Collection<ShippingMethod>,
    val initialStock: Long
) {
    data class Book(
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: String
    )

    data class ShippingMethod(
        val id: String,
        val price: MoneyDto
    )

    data class Category(val id: String)

    data class Images(
        val allImages: List<Image>,
        val thumbnail: Image
    )

    data class Image(
        val url: String
    )
}

package pl.exbook.exbook.listing.domain

import pl.exbook.exbook.offer.domain.Offer.Condition
import pl.exbook.exbook.offer.domain.Offer.Type
import pl.exbook.exbook.shared.*

class DetailedOffer(
    val id: OfferId,
    val book: Book,
    val images: Images,
    val description: String?,
    val type: Type,
    val seller: Seller,
    val cost: Cost?,
    val location: String,
    val categories: Collection<Category>,
    val shippingMethods: Collection<ShippingMethod>
) {
    data class Book(
        val author: String,
        val title: String,
        val isbn: Long?,
        val condition: Condition
    )

    data class Seller(
        val id: UserId,
        val username: String,
        val grade: Double
    )

    data class ShippingMethod(
        val id: ShippingMethodId,
        val name: String,
        val cost: Cost
    )

    data class Images(
        val thumbnail: Image?,
        val otherImages: List<Image>
    )

    data class Category(val id: CategoryId)

    data class Image(val url: String)
}

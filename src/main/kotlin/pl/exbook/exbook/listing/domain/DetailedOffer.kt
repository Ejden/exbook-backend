package pl.exbook.exbook.listing.domain

import pl.exbook.exbook.common.Cost
import pl.exbook.exbook.offer.domain.Offer.Condition
import pl.exbook.exbook.offer.domain.Offer.OfferId
import pl.exbook.exbook.offer.domain.Offer.Type

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
        val id: SellerId,
        val username: String,
        val grade: Double
    )

    data class SellerId(val raw: String)

    data class ShippingMethod(
        val id: ShippingMethodId,
        val name: String,
        val cost: Cost
    )

    data class ShippingMethodId(val raw: String)

    data class Images(
        val thumbnail: Image?,
        val otherImages: List<Image>
    )

    data class Category(val id: CategoryId)

    data class CategoryId(val raw: String)

    data class Image(val url: String)
}

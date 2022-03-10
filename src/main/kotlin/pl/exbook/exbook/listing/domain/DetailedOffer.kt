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
    val price: Money?,
    val location: String,
    val category: Category,
    val shipping: Shipping,
    val inStock: Int
) {
    data class Book(
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: Condition
    )

    data class Seller(
        val id: UserId,
        val username: String,
        val grade: Double
    )

    data class Shipping(
        val shippingMethods: Collection<ShippingMethod>,
        val cheapestMethod: ShippingMethod
    )

    data class ShippingMethod(
        val id: ShippingMethodId,
        val name: String,
        val price: Money
    )

    data class Images(
        val thumbnail: Image?,
        val allImages: List<Image>
    )

    data class Category(val id: CategoryId)

    data class Image(val url: String)
}

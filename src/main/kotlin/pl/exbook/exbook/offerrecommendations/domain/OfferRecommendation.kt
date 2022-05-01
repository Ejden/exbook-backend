package pl.exbook.exbook.offerrecommendations.domain

import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId

data class OfferRecommendation(
    val id: OfferId,
    val book: Book,
    val images: Images,
    val type: Offer.Type,
    val seller: Seller,
    val price: Money?
) {
    data class Book(
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: Offer.Condition
    )

    data class Images(
        val thumbnail: Image?,
        val allImages: List<Image>
    )

    data class Seller(
        val id: UserId,
        val username: String,
        val firstName: String,
        val lastName: String,
        val grade: Double
    )

    data class Category(val id: CategoryId)

    data class Image(val url: String)
}

package pl.exbook.exbook.offer.domain

import pl.exbook.exbook.common.Cost

class Offer(
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
    enum class Type {
        EXCHANGE_AND_BUY, EXCHANGE_ONLY, BUY_ONLY
    }

    data class ShippingMethod(
        val id: ShippingMethodId,
        val cost: Cost
    )

    data class Book(
        val author: String,
        val title: String,
        val isbn: Long?,
        val condition: Condition
    )

    data class Images(
        val thumbnail: Image?,
        val otherImages: Collection<Image>
    )

    data class Seller(val id: SellerId)

    data class Category(val id: CategoryId)

    enum class Condition {
        NEW, PERFECT, LIGHTLY_USED, MODERATELY_USED, BAD
    }

    data class OfferId(val raw: String)

    data class SellerId(val raw: String)

    data class ShippingMethodId(val raw: String)

    data class CategoryId(val raw: String)

    data class Image(val url: String)
}

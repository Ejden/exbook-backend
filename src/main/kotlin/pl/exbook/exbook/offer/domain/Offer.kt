package pl.exbook.exbook.offer.domain

import pl.exbook.exbook.shared.*

class Offer(
    val id: OfferId,
    val book: Book,
    val images: Images,
    val description: String?,
    val type: Type,
    val seller: Seller,
    val price: Money?,
    val location: String,
    val category: Category,
    val shippingMethods: Collection<ShippingMethod>
) {
    enum class Type {
        EXCHANGE_AND_BUY, EXCHANGE_ONLY, BUY_ONLY
    }

    data class ShippingMethod(
        val id: ShippingMethodId,
        val money: Money
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

    data class Seller(val id: UserId)

    data class Category(val id: CategoryId)

    enum class Condition {
        NEW, PERFECT, LIGHTLY_USED, MODERATELY_USED, BAD
    }

    data class Image(val url: String)

    fun canBeBought(): Boolean = this.type == Type.EXCHANGE_AND_BUY || this.type == Type.BUY_ONLY

    fun canBeExchanged(): Boolean = this.type == Type.EXCHANGE_AND_BUY || this.type == Type.EXCHANGE_ONLY
}

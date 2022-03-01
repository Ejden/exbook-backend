package pl.exbook.exbook.offer.domain

import java.time.Instant
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OfferVersionId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.shared.UserId

data class Offer(
    val id: OfferId,
    val versionId: OfferVersionId,
    val versionCreationDate: Instant,
    val versionExpireDate: Instant?,
    val book: Book,
    val images: Images,
    val description: String,
    val type: Type,
    val seller: Seller,
    val price: Money?,
    val location: String,
    val category: Category,
    val shippingMethods: List<ShippingMethod>,
    val stockId: StockId
) {
    enum class Type {
        EXCHANGE_AND_BUY, EXCHANGE_ONLY, BUY_ONLY
    }

    data class ShippingMethod(
        val id: ShippingMethodId,
        val price: Money
    )

    data class Book(
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: Condition
    )

    data class Images(
        val thumbnail: Image?,
        val allImages: List<Image>
    )

    data class Seller(val id: UserId)

    data class Category(val id: CategoryId)

    enum class Condition {
        NEW, PERFECT, LIGHTLY_USED, MODERATELY_USED, BAD
    }

    data class Image(val url: String)

    fun canBeBought(): Boolean = this.type == Type.EXCHANGE_AND_BUY || this.type == Type.BUY_ONLY

    fun canBeExchanged(): Boolean = this.type == Type.EXCHANGE_AND_BUY || this.type == Type.EXCHANGE_ONLY

    fun isActualVersion(): Boolean = this.versionExpireDate != null

    fun deactivate(date: Instant): Offer = this.copy(versionExpireDate = date)
}

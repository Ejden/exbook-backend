package pl.exbook.exbook.offer.domain

import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.ShippingMethodId

data class UpdateOfferCommand(
    val offerId: OfferId,
    val book: Book,
    val images: Images,
    val description: String,
    val type: Offer.Type,
    val price: Money?,
    val location: String,
    val shippingMethods: List<ShippingMethod>
) {
    data class Book(
        val author: String,
        val title: String,
        val isbn: Long?,
        val condition: Offer.Condition
    )

    data class Images(
        val thumbnail: Image?,
        val allImages: List<Image>
    )

    data class Image(
        val url: String
    )

    data class ShippingMethod(
        val id: ShippingMethodId,
        val price: Money
    )
}

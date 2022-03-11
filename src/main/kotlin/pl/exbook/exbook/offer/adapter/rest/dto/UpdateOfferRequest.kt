package pl.exbook.exbook.offer.adapter.rest.dto

import pl.exbook.exbook.offer.domain.Offer.Condition
import pl.exbook.exbook.offer.domain.Offer.Type
import pl.exbook.exbook.offer.domain.UpdateOfferCommand
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.dto.MoneyDto
import pl.exbook.exbook.shared.dto.toDomain

data class UpdateOfferRequest(
    val book: Book,
    val images: Images,
    val description: String,
    val type: String,
    val price: MoneyDto?,
    val location: String,
    val shippingMethods: List<ShippingMethod>
) {
    data class Book(
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: String
    )

    data class Images(
        val thumbnail: Image?,
        val allImages: List<Image>
    )

    data class Image(
        val url: String
    )

    data class ShippingMethod(
        val id: String,
        val price: MoneyDto
    )

    fun toCommand(offerId: OfferId, username: String): UpdateOfferCommand = UpdateOfferCommand(
        offerId = offerId,
        username = username,
        book = this.book.let {
            UpdateOfferCommand.Book(
                author = it.author,
                title = it.title,
                isbn = it.isbn,
                condition = Condition.valueOf(it.condition)
            )
        },
        images = UpdateOfferCommand.Images(
            thumbnail = this.images.thumbnail?.let { UpdateOfferCommand.Image(it.url) },
            allImages = this.images.allImages.map { UpdateOfferCommand.Image(it.url) }
        ),
        description = this.description,
        type = Type.valueOf(this.type),
        price = this.price?.toDomain(),
        location = this.location,
        shippingMethods = this.shippingMethods.map { UpdateOfferCommand.ShippingMethod(ShippingMethodId(it.id), it.price.toDomain()) }
    )
}

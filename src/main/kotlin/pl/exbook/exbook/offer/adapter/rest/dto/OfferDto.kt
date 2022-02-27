package pl.exbook.exbook.offer.adapter.rest.dto

import java.time.Instant
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.dto.MoneyDto
import pl.exbook.exbook.shared.dto.toDto

data class OfferDto(
    val id: String,
    val versionCreationDate: Instant,
    val versionExpireDate: Instant?,
    val book: BookDto,
    val description: String,
    val images: ImagesDto,
    val seller: SellerDto,
    val type: String,
    val cost: MoneyDto?,
    val location: String,
    val shipping: ShippingDto,
    val category: CategoryDto,
    val stock: StockDto
) {

    data class BookDto(
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: String
    )

    data class SellerDto(
        val id: String
    )

    data class ImagesDto(
        val thumbnail: ImageDto?,
        val otherImages: List<ImageDto>
    )

    data class ImageDto(val url: String)

    data class ShippingMethodDto(
        val id: String,
        val cost: MoneyDto
    )

    data class CategoryDto(val id: String)

    data class ShippingDto(
        val shippingMethods: Collection<ShippingMethodDto>,
        val cheapestMethod: ShippingMethodDto
    )

    data class StockDto(
        val id: String
    )

    companion object {
        fun fromDomain(offer: Offer): OfferDto = OfferDto(
            id = offer.id.raw,
            versionCreationDate = offer.versionCreationDate,
            versionExpireDate = offer.versionExpireDate,
            book = offer.book.toDto(),
            description = offer.description,
            images = offer.images.toDto(),
            seller = offer.seller.toDto(),
            type = offer.type.name,
            cost = offer.price?.toDto(),
            location = offer.location,
            shipping = ShippingDto(
                shippingMethods = offer.shippingMethods.map { it.toDto() },
                cheapestMethod = offer.shippingMethods.minByOrNull { it.price }!!.toDto()
            ),
            category = offer.category.toDto(),
            stock = StockDto(
                id = offer.stockId.raw
            )
        )

        private fun Offer.Book.toDto() = BookDto(
            author = this.author,
            title = this.title,
            isbn = this.isbn,
            condition = this.condition.name
        )

        private fun Offer.Images.toDto() = ImagesDto(
            thumbnail = this.thumbnail?.toDto(),
            otherImages = this.allImages.map { it.toDto() }
        )

        private fun Offer.Image.toDto() = ImageDto(this.url)

        private fun Offer.Seller.toDto() = SellerDto(this.id.raw)

        private fun Offer.ShippingMethod.toDto() = ShippingMethodDto(
            id = this.id.raw,
            cost = this.price.toDto()
        )

        private fun Offer.Category.toDto() = CategoryDto(this.id.raw)
    }
}

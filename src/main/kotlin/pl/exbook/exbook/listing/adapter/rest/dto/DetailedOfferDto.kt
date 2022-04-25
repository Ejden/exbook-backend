package pl.exbook.exbook.listing.adapter.rest.dto

import pl.exbook.exbook.listing.domain.DetailedOffer
import pl.exbook.exbook.shared.dto.MoneyDto
import pl.exbook.exbook.shared.dto.toDto

data class DetailedOfferDto(
    val id: String,
    val book: BookDto,
    val images: ImagesDto,
    val description: String?,
    val type: String,
    val seller: SellerDto,
    val price: MoneyDto?,
    val location: String,
    val category: CategoryDto,
    val shipping: ShippingDto,
    val inStock: Long
) {
    companion object {
        fun fromDomain(offer: DetailedOffer) = DetailedOfferDto(
            id = offer.id.raw,
            book = offer.book.toDto(),
            images = offer.images.toDto(),
            description = offer.description,
            type = offer.type.name,
            seller = offer.seller.toDto(),
            price = offer.price?.toDto(),
            location = offer.location,
            category = offer.category.toDto(),
            shipping = offer.shipping.toDto(),
            inStock = offer.inStock
        )
    }
}

data class BookDto(
    val author: String,
    val title: String,
    val isbn: String?,
    val condition: String
)

data class ImagesDto(
    val thumbnail: ImageDto?,
    val allImages: Collection<ImageDto>
)

data class ImageDto(val url: String)

data class SellerDto(
    val id: String,
    val username: String,
    val grade: Double
)

data class CategoryDto(val id: String)

data class ShippingDto(
    val shippingMethods: Collection<ShippingMethodDto>,
    val cheapestMethod: ShippingMethodDto
)

data class ShippingMethodDto(
    val id: String,
    val name: String,
    val price: MoneyDto
)

private fun DetailedOffer.Book.toDto() = BookDto(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition.name
)

private fun DetailedOffer.Images.toDto() = ImagesDto(
    thumbnail = this.thumbnail?.toDto(),
    allImages = this.allImages.map { it.toDto() }
)

private fun DetailedOffer.Image.toDto() = ImageDto(this.url)

private fun DetailedOffer.Seller.toDto() = SellerDto(
    id = this.id.raw,
    username = this.username,
    grade = this.grade
)

private fun DetailedOffer.Category.toDto() = CategoryDto(this.id.raw)

private fun DetailedOffer.Shipping.toDto() = ShippingDto(
    shippingMethods = this.shippingMethods.map { it.toDto() },
    cheapestMethod = this.cheapestMethod.toDto()
)

private fun DetailedOffer.ShippingMethod.toDto() = ShippingMethodDto(
    id = this.id.raw,
    name = this.name,
    price = this.price.toDto()
)

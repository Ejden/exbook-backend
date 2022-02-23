package pl.exbook.exbook.listing.adapter.rest

import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import pl.exbook.exbook.listing.ListingFacade
import pl.exbook.exbook.listing.domain.DetailedOffer
import pl.exbook.exbook.shared.ContentType
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.dto.MoneyDto
import pl.exbook.exbook.shared.dto.toDto

@RestController
@RequestMapping("api/listing")
class ListingEndpoint(
    private val listingFacade: ListingFacade
) {

    @GetMapping(produces = [ContentType.V1])
    fun getOfferListing(@RequestParam offersPerPage: Int?, @RequestParam page: Int?, @RequestParam sorting: String?): Page<DetailedOfferDto> {
        return listingFacade.getOfferListing(offersPerPage, page, sorting).map { it.toDto() }
    }

    @GetMapping("{offerId}", produces = [ContentType.V1])
    fun getOffer(@PathVariable offerId: OfferId) = listingFacade.getOffer(offerId).toDto()
}

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
    val shipping: ShippingDto
)

data class BookDto(
    val author: String,
    val title: String,
    val isbn: Long?,
    val condition: String
)

data class ImagesDto(
    val thumbnail: ImageDto?,
    val otherImages: Collection<ImageDto>
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

private fun DetailedOffer.toDto() = DetailedOfferDto(
    id = this.id.raw,
    book = this.book.toDto(),
    images = this.images.toDto(),
    description = this.description,
    type = this.type.name,
    seller = this.seller.toDto(),
    price = this.price?.toDto(),
    location = this.location,
    category = this.category.toDto(),
    shipping = this.shipping.toDto()
)

private fun DetailedOffer.Book.toDto() = BookDto(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition.name
)

private fun DetailedOffer.Images.toDto() = ImagesDto(
    thumbnail = this.thumbnail?.toDto(),
    otherImages = this.otherImages.map { it.toDto() }
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

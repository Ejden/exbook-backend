package pl.exbook.exbook.listing.adapter.rest

import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.shared.Cost
import pl.exbook.exbook.listing.ListingFacade
import pl.exbook.exbook.listing.domain.DetailedOffer
import pl.exbook.exbook.shared.ContentType
import pl.exbook.exbook.util.parseMoneyToString

@RestController
@RequestMapping("/listing")
class ListingEndpoint(
    private val listingFacade: ListingFacade
) {

    @GetMapping(produces = [ContentType.V1])
    fun getOfferListing(@RequestParam offersPerPage: Int?, @RequestParam page: Int?, @RequestParam sorting: String?): Page<DetailedOfferDto> {
        return listingFacade.getOfferListing(offersPerPage, page, sorting).map { it.toDto() }
    }
}

data class DetailedOfferDto(
    val id: String,
    val book: BookDto,
    val images: ImagesDto,
    val description: String?,
    val type: String,
    val seller: SellerDto,
    val cost: CostDto?,
    val location: String,
    val category: CategoryDto,
    val shippingMethods: Collection<ShippingMethodDto>
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

data class ShippingMethodDto(
    val id: String,
    val name: String,
    val cost: CostDto
)

data class CostDto(
    val value: String,
    val currency: String
)

private fun DetailedOffer.toDto() = DetailedOfferDto(
    id = this.id.raw,
    book = this.book.toDto(),
    images = this.images.toDto(),
    description = this.description,
    type = this.type.name,
    seller = this.seller.toDto(),
    cost = this.cost?.toDto(),
    location = this.location,
    category = this.category.toDto(),
    shippingMethods = this.shippingMethods.map { it.toDto() }
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

private fun Cost.toDto() = CostDto(
    value = parseMoneyToString(this.value),
    currency = this.currency.name
)

private fun DetailedOffer.Category.toDto() = CategoryDto(this.id.raw)

private fun DetailedOffer.ShippingMethod.toDto() = ShippingMethodDto(
    id = this.id.raw,
    name = this.name,
    cost = cost.toDto()
)

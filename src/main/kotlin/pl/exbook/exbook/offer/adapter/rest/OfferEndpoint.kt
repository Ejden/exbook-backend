package pl.exbook.exbook.offer.adapter.rest

import mu.KLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.common.Currency
import pl.exbook.exbook.common.dto.CostDto
import pl.exbook.exbook.common.dto.toDto
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.user.UserNotFoundException

@RestController
@RequestMapping("api/v1/offers")
class OfferEndpoint(private val offerFacade: OfferFacade) {

    companion object : KLogging()

    @PostMapping
    @PreAuthorize("hasAuthority('EXCHANGE_BOOKS')")
    fun addOffer(@RequestBody offer: NewOfferRequest, user: UsernamePasswordAuthenticationToken?): ResponseEntity<OfferDto> {
        return if (user != null) {
            ResponseEntity.ok(offerFacade.addOffer(offer, user).toDto())
        } else {
            logger.warn { "Non logged user tried to add new offer" }
            throw UserNotFoundException("Non logged user tried to add new offer")
        }
    }

    @GetMapping("{offerId}")
    fun getOffer(@PathVariable offerId: Offer.OfferId): OfferDto? {
        return offerFacade.getOffer(offerId).toDto()
    }
}

data class NewOfferRequest(
    val book: Book,
    val description: String?,
    val categories: Collection<Category>,
    val type: Offer.Type,
    val cost: Cost?,
    val location: String,
    val shippingMethods: Collection<ShippingMethod>
) {
    data class Book(
        val author: String,
        val title: String,
        val isbn: Long?,
        val condition: Offer.Condition
    )

    data class Cost(
        val value: String,
        val currency: Currency
    )

    data class ShippingMethod(
        val id: String,
        val cost: Cost
    )

    data class Category(val id: String)
}

data class OfferDto(
    val id: String,
    val book: BookDto,
    val description: String?,
    val images: ImagesDto,
    val seller: SellerDto,
    val type: String,
    val cost: CostDto?,
    val location: String,
    val shippingMethods: Collection<ShippingMethodDto>,
    val categories: Collection<CategoryDto>
) {

    data class BookDto(
        val author: String,
        val title: String,
        val isbn: Long?,
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
        val cost: CostDto
    )

    data class CategoryDto(val id: String)
}

private fun Offer.toDto() = OfferDto(
    id = this.id.raw,
    book = this.book.toDto(),
    description = this.description,
    images = this.images.toDto(),
    seller = this.seller.toDto(),
    type = this.type.name,
    cost = this.cost?.toDto(),
    location = this.location,
    shippingMethods = this.shippingMethods.map { it.toDto() },
    categories = this.categories.map { it.toDto() }
)

private fun Offer.Book.toDto() = OfferDto.BookDto(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition.name
)

private fun Offer.Images.toDto() = OfferDto.ImagesDto(
    thumbnail = this.thumbnail?.toDto(),
    otherImages = this.otherImages.map { it.toDto() }
)

private fun Offer.Image.toDto() = OfferDto.ImageDto(this.url)

private fun Offer.Seller.toDto() = OfferDto.SellerDto(this.id.raw)

private fun Offer.ShippingMethod.toDto() = OfferDto.ShippingMethodDto(
    id = this.id.raw,
    cost = this.cost.toDto()
)

private fun Offer.Category.toDto() = OfferDto.CategoryDto(this.id.raw)

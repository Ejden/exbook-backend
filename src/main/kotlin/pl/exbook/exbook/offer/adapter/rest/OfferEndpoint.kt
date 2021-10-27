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
import pl.exbook.exbook.shared.Currency
import pl.exbook.exbook.shared.dto.MoneyDto
import pl.exbook.exbook.shared.dto.toDto
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.ContentType
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.user.UserNotFoundException

@RestController
@RequestMapping("api/offers")
class OfferEndpoint(private val offerFacade: OfferFacade) {

    companion object : KLogging()

    @PostMapping(produces = [ContentType.V1])
    @PreAuthorize("hasAuthority('EXCHANGE_BOOKS')")
    fun addOffer(@RequestBody offer: NewOfferRequest, user: UsernamePasswordAuthenticationToken?): ResponseEntity<OfferDto> {
        return if (user != null) {
            ResponseEntity.ok(offerFacade.addOffer(offer, user).toDto())
        } else {
            logger.warn { "Non logged user tried to add new offer" }
            throw UserNotFoundException("Non logged user tried to add new offer")
        }
    }

    @GetMapping("{offerId}", produces = [ContentType.V1])
    fun getOffer(@PathVariable offerId: OfferId): OfferDto {
        return offerFacade.getOffer(offerId).toDto()
    }
}

data class NewOfferRequest(
    val book: Book,
    val description: String?,
    val category: String,
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
    val cost: MoneyDto?,
    val location: String,
    val shipping: ShippingDto,
    val category: CategoryDto
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
        val cost: MoneyDto
    )

    data class CategoryDto(val id: String)

    data class ShippingDto(
        val shippingMethods: Collection<ShippingMethodDto>,
        val cheapestMethod: ShippingMethodDto
    )
}

private fun Offer.toDto() = OfferDto(
    id = this.id.raw,
    book = this.book.toDto(),
    description = this.description,
    images = this.images.toDto(),
    seller = this.seller.toDto(),
    type = this.type.name,
    cost = this.price?.toDto(),
    location = this.location,
    shipping = OfferDto.ShippingDto(
        shippingMethods = this.shippingMethods.map { it.toDto() },
        cheapestMethod = this.shippingMethods.minByOrNull { it.money }!!.toDto()
    ),
    category = this.category.toDto()
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
    cost = this.money.toDto()
)

private fun Offer.Category.toDto() = OfferDto.CategoryDto(this.id.raw)

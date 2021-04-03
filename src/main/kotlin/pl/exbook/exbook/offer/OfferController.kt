package pl.exbook.exbook.offer

import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import pl.exbook.exbook.shipping.ShippingMethod

@RestController
@RequestMapping("api/v1/offers")
@PreAuthorize("isAuthenticated()")
class OfferController(private val offerService: OfferService) {

    @GetMapping
    @PreAuthorize("hasAuthority('SEARCH_BOOKS')")
    fun getOfferListing(@RequestParam offersPerPage: Int?, @RequestParam page: Int?, @RequestParam sorting: String?): Page<OfferDto> {
        return offerService.getOfferListing(offersPerPage, page, sorting)
            .map { offer -> offer.toOfferDto() }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EXCHANGE_BOOKS')")
    fun addOffer(@RequestBody offer: NewOfferRequest, user: UsernamePasswordAuthenticationToken?): OfferDto? {
        return if (user != null) {
            offerService.addOffer(offer, user)?.toOfferDto()
        } else
            null
    }

    @GetMapping("{offerId}")
    fun getOffer(@PathVariable offerId: String): OfferDto? {
        return offerService.getOffer(offerId)?.toOfferDto()
    }
}

data class NewOfferRequest(
    val book: Book,
    val description: String?,
    val images: Images,
    val categories: Collection<String>,
    val type: Offer.Type,
    val price: Int?,
    val location: String,
    val shippingMethods: Collection<ShippingMethod>
)

data class OfferDto(
    val id: String,
    val book: Book,
    val description: String?,
    val images: Images,
    val seller: Seller,
    val type: Offer.Type,
    val price: Int?,
    val location: String,
    val shippingMethods: Collection<ShippingMethod>,
    val categories: Collection<String>
) {

    data class Seller(
        val id: String,
        val username: String,
        val grade: Double
    )
}

data class OfferListingDto(
    val id: String
)
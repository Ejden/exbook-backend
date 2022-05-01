package pl.exbook.exbook.listing.adapter.rest

import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import pl.exbook.exbook.listing.ListingFacade
import pl.exbook.exbook.listing.adapter.rest.dto.DetailedOfferDto
import pl.exbook.exbook.listing.domain.DetailedOffer
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.ContentType
import pl.exbook.exbook.shared.OfferId

@RestController
@RequestMapping("api")
class ListingEndpoint(private val listingFacade: ListingFacade) {
    @GetMapping("listing", produces = [ContentType.V1])
    fun getOfferListing(
        @RequestParam("perPage") offersPerPage: Int?,
        @RequestParam("page") page: Int?,
        @RequestParam("sort") sorting: String?,
        @RequestParam("search") searchingPhrase: String,
        @RequestParam("condition") bookConditions: List<String>?,
        @RequestParam("offerType") offerType: List<String>?,
        @RequestParam("priceFrom") priceFrom: String?,
        @RequestParam("priceTo") priceTo: String?,
        @RequestParam("location") location: String?,
        @RequestParam("category") categoryId: String?
    ): Page<DetailedOfferDto> {
        return listingFacade.getOfferListing(
            searchingPhrase,
            bookConditions?.map { Offer.Condition.valueOf(it) },
            offerType?.map {
                if (it == "BUY") listOf(Offer.Type.EXCHANGE_AND_BUY, Offer.Type.BUY_ONLY) else listOf(Offer.Type.EXCHANGE_AND_BUY, Offer.Type.EXCHANGE_ONLY)
            }?.flatten()?.distinct(),
            priceFrom?.toBigDecimal(),
            priceTo?.toBigDecimal(),
            location,
            categoryId?.let { CategoryId(it) },
            offersPerPage,
            page,
            sorting
        ).map { it.toDto() }
    }

    @GetMapping("listing/{offerId}", produces = [ContentType.V1])
    fun getOffer(@PathVariable offerId: OfferId): DetailedOfferDto = listingFacade.getOffer(offerId).toDto()

    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping("sale/offers")
    fun getUserOffers(
        token: UsernamePasswordAuthenticationToken,
        @RequestParam("size") offersPerPage: Int?,
        @RequestParam("p") page: Int?,
        @RequestParam sorting: String?
    ): Page<DetailedOfferDto> = listingFacade.getUserOffers(token.name, offersPerPage, page, sorting).map { it.toDto() }
}

private fun DetailedOffer.toDto() = DetailedOfferDto.fromDomain(this)

package pl.exbook.exbook.listing.adapter.rest

import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import pl.exbook.exbook.listing.ListingFacade
import pl.exbook.exbook.listing.adapter.rest.dto.DetailedOfferDto
import pl.exbook.exbook.listing.domain.DetailedOffer
import pl.exbook.exbook.shared.ContentType
import pl.exbook.exbook.shared.OfferId

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
    fun getOffer(@PathVariable offerId: OfferId): DetailedOfferDto = listingFacade.getOffer(offerId).toDto()
}

private fun DetailedOffer.toDto() = DetailedOfferDto.fromDomain(this)

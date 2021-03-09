package pl.exbook.exbook.offer

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.util.stream.Collectors

@RestController
@RequestMapping("api/v1/books")
@PreAuthorize("isAuthenticated()")
class OfferController (private val offerService: OfferService) {

    @GetMapping
    @PreAuthorize("hasAuthority('SEARCH_BOOKS')")
    fun getAllBooks() : MutableCollection<OfferDto> {
        return offerService.getAllOffers()
            .stream()
            .map(Offer::toOfferDto)
            .collect(Collectors.toList())
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EXCHANGE_BOOKS')")
    fun addBook(@RequestBody book : NewBookRequest, user : UsernamePasswordAuthenticationToken?) : OfferDto? {
        return if (user != null) {
            offerService.addOffer(book, user).toOfferDto()
        } else
            null
    }
}

data class NewBookRequest(
    val author: String,
    val title: String,
    val ISBN: Long?,
    val description: String?,
    val condition: Condition
) {

}

data class OfferDto(
    val id: String,
    val book: Book,
    val description: String?,
    val images: Images,
    val seller: Seller
) {

    data class Seller(
        val id: String
    ) {

    }
}
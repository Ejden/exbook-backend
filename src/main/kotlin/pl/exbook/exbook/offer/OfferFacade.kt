package pl.exbook.exbook.offer

import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import pl.exbook.exbook.offer.adapter.mongodb.OfferDatabaseModel
import pl.exbook.exbook.offer.adapter.mongodb.OfferNotFoundException
import pl.exbook.exbook.offer.adapter.mongodb.OfferRepository
import pl.exbook.exbook.offer.adapter.rest.NewOfferRequest
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.user.User
import pl.exbook.exbook.user.UserService
import java.util.stream.Collectors

private val logger = KotlinLogging.logger {}

@Service
class OfferService (
    private val offerRepository: OfferRepository,
    private val userService: UserService
){
    private val MAX_NUMBER_OF_OFFERS_PER_PAGE = 100

    fun getAllOffers() : MutableList<Offer> {
        return offerRepository.findAll()
            .stream()
            .map(OfferDatabaseModel::toOffer)
            .collect(Collectors.toList())
    }

    fun getOfferListing(offersPerPage: Int?, page: Int?, sorting: String?): Page<Offer> {
        val pageRequest = parsePageRequest(offersPerPage, page, sorting)
        return offerRepository.findAll(pageRequest)
            .map { offer ->
                val seller = userService.findById(offer.sellerId)
                offer.toOffer(seller)
            }
    }

    private fun parsePageRequest(_offersPerPage: Int?, _page: Int?, _sorting: String?): Pageable {
        var offersPerPage = _offersPerPage
        var page = _page
        var sorting = _sorting

        if (offersPerPage == null) offersPerPage = 50
        if (offersPerPage > MAX_NUMBER_OF_OFFERS_PER_PAGE) offersPerPage = MAX_NUMBER_OF_OFFERS_PER_PAGE

        if (page == null) page = 0

        if (sorting == null)
            return PageRequest.of(page, offersPerPage)

        return PageRequest.of(page, offersPerPage, parseSorting(sorting))
    }
    /**
    sorting:
        - p for price
        - d for date of publication
        each is followed by
        - d for descending
        - a for ascending
     **/
    private fun parseSorting(sorting: String): Sort {
        val sort = when (sorting.first()) {
            'p' -> Sort.by("price")
            else -> Sort.by("publicationDate")
        }

        if (sorting.last() == 'a') {
            sort.ascending()
        } else {
            sort.descending()
        }

        return sort
    }

    fun getOffer(offerId: String): Offer? {
        val databaseOffer = offerRepository.findById(offerId).orElseThrow { OfferNotFoundException() }
        val seller = userService.findById(databaseOffer.sellerId)

        return databaseOffer.toOffer(seller)
    }

    fun addOffer(request: NewOfferRequest, token: UsernamePasswordAuthenticationToken) : Offer? {
        // Getting user from database that sent
        val user : User? = userService.findUserByUsername(token.name)
        if (request.price != null) {
            if (request.price < 0) {
                return null
            }
        }

        val offer =  offerRepository.save(
            OfferDatabaseModel(
                id = null,
                book = request.book,
                images = request.images,
                description = request.description,
                sellerId = user?.id!!,
                type = request.type,
                price = request.price,
                location = request.location,
                categories = request.categories,
                shippingMethods = request.shippingMethods
            )
        ).toOffer(user)

        logger.debug("User with id = ${user.id} added new offer with id = ${offer.id}")

        return offer
    }
}
